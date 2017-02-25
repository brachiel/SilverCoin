package ch.chrummibei.silvercoin.universe.trade;

import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.credit.TotalValue;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import ch.chrummibei.silvercoin.universe.item.Item;
import com.badlogic.ashley.core.Entity;

import java.util.Comparator;

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
    }
}
