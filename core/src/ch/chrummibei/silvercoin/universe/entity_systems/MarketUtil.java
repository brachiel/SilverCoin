package ch.chrummibei.silvercoin.universe.entity_systems;

import ch.chrummibei.silvercoin.universe.components.MarketComponent;
import ch.chrummibei.silvercoin.universe.components.MarketSightComponent;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A market is a list of TradeOffers that makes them comparable and searchable.
 */
public class MarketUtil {
    /**
     * Searches through the offered trades for the first credit that
     * passes cmp with true. Returns null if none pass cmp.
     * @param cmp A comparator function
     * @return credit or null
     */
    public static Stream<TradeOffer> searchOfferedTrades(MarketComponent market, Predicate<TradeOffer> cmp) {
        return market.offeredTrades.stream().filter(cmp);
    }

    public static Stream<TradeOffer> searchOfferedTrades(MarketComponent market) {
        return market.offeredTrades.stream();
    }

    public static Stream<TradeOffer> searchOfferedTrades(MarketSightComponent marketSight, Predicate<TradeOffer> cmp) {
        return marketSight.markets.parallelStream().flatMap(market -> searchOfferedTrades(market, cmp)).distinct();
    }

    public static Stream<TradeOffer> searchOfferedTrades(MarketSightComponent marketSight) {
        return marketSight.markets.parallelStream().flatMap(market -> searchOfferedTrades(market)).distinct();
    }

    public static Comparator<TradeOffer> bestPriceComparator() {
        return Comparator.comparingDouble(TradeOffer::getSignedPriceDouble);
    }

    /**
     * Searches through the offered trades for the first credit of item.
     * If none is found, null is returned.
     * @param item Item to be filtered
     * @return credit or null
     */
    public static Stream<TradeOffer> searchOfferedTrades(MarketComponent market, Item item, TradeOffer.TYPE type) {
        return searchOfferedTrades(market, offer -> offer.getItem() == item && offer.getType() == type);
    }

    public static Stream<TradeOffer> searchOfferedTrades(MarketSightComponent marketSight, Item item, TradeOffer.TYPE type) {
        return searchOfferedTrades(marketSight, offer -> offer.getItem() == item && offer.getType() == type);
    }

    public static Optional<TradeOffer> searchBestSellingTrade(MarketComponent market, Item item) {
        return searchOfferedTrades(market, item, TradeOffer.TYPE.SELLING)
                .min(Comparator.comparing(t -> t.getPrice().toDouble()));
    }

    public static Optional<TradeOffer> searchBestBuyingTrade(MarketComponent market, Item item) {
        return searchOfferedTrades(market, item, TradeOffer.TYPE.BUYING)
                .max(Comparator.comparing(t -> t.getPrice().toDouble()));
    }

    /**
     * Search all viewable markets in marketSight for the best prices to trade item. Return a Map
     * of TradeOffers with the amount to trade with each offer. The last offer might be a partial trade.
     * @param marketSight Component of visible markets
     * @param item Item to be traded
     * @param type Trade type (BUYING or SELLING)
     * @param amount Amount to trade.
     * @return Might return an empty list or a list with not enough trades to fully trade amount.
     */
    public static Map<TradeOffer,Integer> getTradeOffersToTradeAmount(MarketSightComponent marketSight,
                                                                      Item item,
                                                                      TradeOffer.TYPE type,
                                                                      int amount) {
        HashMap<TradeOffer,Integer> tradeOffersToAccept = new HashMap<>();

        int amountLeftToTrade = amount;

        List<TradeOffer> sortedTradeOffers = searchOfferedTrades(marketSight, item, type)
                .filter(offer -> offer.getAmount() != 0)
                .sorted(bestPriceComparator())
                .collect(Collectors.toList());


        for (TradeOffer tradeOffer : sortedTradeOffers) {
            int amountToTradeWithThisOffer = Math.min(amountLeftToTrade, tradeOffer.getAmount());
            amountLeftToTrade -= amountToTradeWithThisOffer;

            tradeOffersToAccept.put(tradeOffer, amountToTradeWithThisOffer);

            if (amountLeftToTrade <= 0) {
                // Last Buy
                break;
            }
        }

        return tradeOffersToAccept;
    }

    public static Stream<Item> searchTradedItems(MarketSightComponent marketSight) {
        return searchOfferedTrades(marketSight).map(TradeOffer::getItem).distinct();
    }

}
