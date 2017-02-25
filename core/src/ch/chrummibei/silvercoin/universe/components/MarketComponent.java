package ch.chrummibei.silvercoin.universe.components;

import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by brachiel on 20/02/2017.
 */
public class MarketComponent implements Component {
    HashSet<Entity> traders = new HashSet<>();

    public void addTrader(Entity entity) {
        traders.add(entity);
    }

    public void removeTrader(Entity entity) {
        traders.remove(entity);
    }

    /* Trade filters */

    public Stream<TradeOffer> collectTradeOffers() {
        return traders.stream().map(Mappers.trader::get).flatMap(TraderComponent::getTradeOffers);
    }

    public Stream<TradeOffer> filterTradeOffers(Predicate<TradeOffer> cmp) {
        return collectTradeOffers().filter(cmp);
    }

    /**
     * Searches through the offered trades for the first credit of item.
     * If none is found, null is returned.
     * @param item Item to be filtered
     * @return credit or null
     */
    public Stream<TradeOffer> filterTradeOffers(Item item, TradeOffer.TYPE type) {
        return filterTradeOffers(offer -> offer.getItem() == item && offer.getType() == type);
    }

    public Stream<TradeOffer> filterAndSortByBestPrice(Item item, TradeOffer.TYPE type) {
        return filterTradeOffers(item, type).sorted(TradeOffer.bestPriceComparator);
    }

    public Optional<TradeOffer> searchBestSellingTrade(Item item) {
        return filterTradeOffers(item, TradeOffer.TYPE.SELLING).min(TradeOffer.bestPriceComparator);
    }

    public Optional<TradeOffer> searchBestBuyingTrade(Item item) {
        return filterTradeOffers(item, TradeOffer.TYPE.BUYING).max(TradeOffer.bestPriceComparator);
    }

    /**
     * Search all viewable markets in marketSight for the best prices to trade item. Return a Map
     * of TradeOffers with the amount to trade with each offer. The last offer might be a partial trade.
     * @param item Item to be traded
     * @param type Trade type (BUYING or SELLING)
     * @param amount Amount to trade.
     * @return Might return an empty list or a list with not enough trades to fully trade amount.
     */
    public Map<TradeOffer,Integer> searchTradeOffersToTradeAmount(Item item,
                                                                  TradeOffer.TYPE type,
                                                                  int amount) {
        HashMap<TradeOffer,Integer> tradeOffersToAccept = new HashMap<>();

        int amountLeftToTrade = Math.abs(amount);

        List<TradeOffer> sortedTradeOffers = filterTradeOffers(item, type)
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
