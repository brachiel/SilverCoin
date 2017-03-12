package ch.chrummibei.silvercoin.universe.trade;

import ch.chrummibei.silvercoin.constants.Messages;
import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.credit.TotalValue;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import ch.chrummibei.silvercoin.universe.entity_systems.TraderSystem;
import ch.chrummibei.silvercoin.universe.item.Item;
import com.badlogic.ashley.core.Entity;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A TradeOffer is a potential Trade offered by a TraderComponent.
 */
public class TradeOffer {
    public enum TYPE {
        BUYING("Buy", "Buying", "is buying"), SELLING("Sell", "Selling", "is selling");

        TYPE(final String shortString,
             final String longString,
             final String verb) {
            this.shortString = shortString;
            this.longString = longString;
            this.verb = verb;
        }
        public String toString() { return this.longString; }

        public final String shortString; /* The string representation of this type */
        public final String longString; /* The string representation of this type */
        public final String verb; /* The string representation of this type */

        public TYPE opposite() { if (this == BUYING) return SELLING; else return BUYING; }

        public static TYPE fromAmount(int amount) {
            if (amount == 0) throw new RuntimeException("TYPE amount = 0. This is a bug.");
            return (amount > 0) ? TYPE.BUYING : TYPE.SELLING;
        }
    }

    public static final Comparator<TradeOffer> bestPriceComparator
            = Comparator.comparingDouble(TradeOffer::getSignedPriceDouble);

    private final Entity offeringTrader;
    private final Item item;
    private final TYPE type;
    private int amount;
    private Price price;
    private Trade resultingTrade = null;

    public TradeOffer(Entity offeringTrader, Item item, TYPE type, int amount, Price price) {
        this.offeringTrader = offeringTrader;
        this.item = item;
        this.type = type;
        this.amount = amount;
        this.price = price;

        if (amount <= 0) {
            throw new RuntimeException("TradeOffer with amount <= 0. This is a bug.");
        }

        Universe.messageDispatcher.dispatchMessage(Messages.TRADE_OFFER_ADDED, this);
    }


    public boolean isSelling() {
        return type == TYPE.SELLING;
    }

    public boolean isBuying() {
        return type == TYPE.BUYING;
    }

    public Entity getOfferingTrader() {
        return offeringTrader;
    }

    public int getAmount() {
        return amount;
    }

    public void reduceAmountBy(int amount) {
        this.amount -= amount;
    }

    public void updateAmount(int amount) { // Same price
        this.amount = amount;
    }

    public void updateAmount(int amount, Price price) {
        this.amount = amount;
        this.price = price;

        if (amount < 0) {
            throw new RuntimeException("TradeOffer with amount < 0. This is a bug.");
        }
    }

    public void addAmount(int amount, Price price) {
        if (amount == 0) throw new RuntimeException("Trying to divide by 0");
        this.price = this.price.toTotalValue(this.amount).add(price.toTotalValue(amount)).toPrice(this.amount + amount);
        this.amount += amount;

        if (amount < 0) {
            throw new RuntimeException("TradeOffer with amount < 0. This is a bug.");
        }
    }

    public Item getItem() {
        return item;
    }

    public TYPE getType() {
        return type;
    }

    public Price getPrice() {
        return price;
    }

    public double getSignedPriceDouble() {
        if (isBuying()) { // If buying, the highest prices are best. So we need to order descending.
            return -getPrice().toDouble();
        } else {
            return getPrice().toDouble();
        }
    }

    public TotalValue getTotalValue() {
        return price.toTotalValue(amount);
    }

    public String toString() {
        String string;
        if (Mappers.named.has(offeringTrader)) {
            string = Mappers.named.get(offeringTrader).name;
        } else {
            string = offeringTrader.toString();
        }
        return string + " " + type.verb + " " + getAmount() + " " + item.getName() + " for " + price;
    }

    public String compactString() {
        return price + " (" + offeringTrader + ")";
    }


    public void accept(Entity acceptingTrader, int acceptingAmount) {
        if (this.amount < acceptingAmount) {
            throw new RuntimeException("Trader has not enough amount left. This is a bug");
        }

        resultingTrade = new Trade(this, acceptingTrader, acceptingAmount);
        // The offer decreases its acceptingAmount that is offered.
        this.amount -= acceptingAmount;

        Mappers.trader.get(acceptingTrader).acceptedTrades.add(resultingTrade);
        Mappers.trader.get(offeringTrader).acceptedTrades.add(resultingTrade);

        TraderSystem.integrityCheck(offeringTrader);

        Universe.messageDispatcher.dispatchMessage(Messages.TRADE_OFFER_ACCEPTED, this);
        if (this.amount <= 0) {
            Universe.messageDispatcher.dispatchMessage(Messages.TRADE_OFFER_FULLY_ACCEPTED, this);
        }
    }

    public void remove() {
        Universe.messageDispatcher.dispatchMessage(Messages.TRADE_OFFER_REMOVED, this);
    }


    /*** STATIC METHODS ***/


    public static Stream<TradeOffer> filterTradeOffers(Stream<TradeOffer> tradeOffers, Predicate<TradeOffer> cmp) {
        return tradeOffers.filter(cmp);
    }

    /**
     * Searches through the offered trades for the first credit of item.
     * If none is found, null is returned.
     * @param item Item to be filtered
     * @return credit or null
     */
    public static Stream<TradeOffer> filterTradeOffers(Stream<TradeOffer> tradeOffers, Item item, TradeOffer.TYPE type) {
        return filterTradeOffers(tradeOffers, offer -> offer.getItem() == item && offer.getType() == type);
    }

    public static Stream<TradeOffer> filterAndSortByBestPrice(Stream<TradeOffer> tradeOffers, Item item, TradeOffer.TYPE type) {
        return filterTradeOffers(tradeOffers, item, type).sorted(TradeOffer.bestPriceComparator);
    }

    public static Optional<TradeOffer> searchBestSellingTrade(Stream<TradeOffer> tradeOffers, Item item) {
        return filterTradeOffers(tradeOffers, item, TradeOffer.TYPE.SELLING).min(TradeOffer.bestPriceComparator);
    }

    public static Optional<TradeOffer> searchBestBuyingTrade(Stream<TradeOffer> tradeOffers, Item item) {
        return filterTradeOffers(tradeOffers, item, TradeOffer.TYPE.BUYING).max(TradeOffer.bestPriceComparator);
    }

    /**
     * Search all viewable markets in marketAccess for the best prices to trade item. Return a Map
     * of TradeOffers with the amount to trade with each offer. The last offer might be a partial trade.
     * @param item Item to be traded
     * @param type Trade type (BUYING or SELLING)
     * @param amount Amount to trade.
     * @return Might return an empty list or a list with not enough trades to fully trade amount.
     */
    public static Map<TradeOffer,Integer> searchTradeOffersToTradeAmount(Stream<TradeOffer> tradeOffers,
                                                                         Item item,
                                                                         TradeOffer.TYPE type,
                                                                         int amount) {
        HashMap<TradeOffer,Integer> tradeOffersToAccept = new HashMap<>();

        int amountLeftToTrade = Math.abs(amount);

        List<TradeOffer> sortedTradeOffers = filterTradeOffers(tradeOffers, item, type)
                .filter(offer -> offer.getAmount() != 0)
                .sorted(TradeOffer.bestPriceComparator)
                .collect(Collectors.toList());


        for (TradeOffer tradeOffer : sortedTradeOffers) {
            int amountToTradeWithThisOffer = Math.min(amountLeftToTrade, tradeOffer.getAmount());
            amountLeftToTrade -= amountToTradeWithThisOffer;

            if (amountToTradeWithThisOffer < 0) throw new RuntimeException("Trading <0? Are you crazy?");

            tradeOffersToAccept.put(tradeOffer, amountToTradeWithThisOffer);

            if (amountLeftToTrade <= 0) {
                // Last Buy
                break;
            }
        }

        return tradeOffersToAccept;
    }
}
