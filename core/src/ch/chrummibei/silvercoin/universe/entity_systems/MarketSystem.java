package ch.chrummibei.silvercoin.universe.entity_systems;

import ch.chrummibei.silvercoin.universe.components.MarketComponent;
import ch.chrummibei.silvercoin.universe.components.MarketSightComponent;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A market is a list of TradeOffers that makes them comparable and searchable.
 */
public class MarketSystem {
    public static Stream<TradeOffer> filterTradeOffers(MarketSightComponent marketSight, Predicate<TradeOffer> cmp) {
        return marketSight.markets.parallelStream().flatMap(market -> market.filterTradeOffers(cmp)).distinct();
    }


    public static Stream<TradeOffer> filterTradeOffers(MarketSightComponent marketSight) {
        return marketSight.markets.parallelStream().flatMap(MarketComponent::collectTradeOffers);
    }

    public static Stream<TradeOffer> filterTradeOffers(MarketSightComponent marketSight, Item item, TradeOffer.TYPE type) {
        return filterTradeOffers(marketSight, offer -> offer.getItem() == item && offer.getType() == type);
    }

    public static Stream<Item> searchTradedItems(MarketSightComponent marketSight) {
        return filterTradeOffers(marketSight).map(TradeOffer::getItem).distinct();
    }
}
