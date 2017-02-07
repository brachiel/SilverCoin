package ch.chrummibei.silvercoin.universe.trade;

import ch.chrummibei.silvercoin.universe.item.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A market is a list of TradeOffers that makes them comparable and searchable.
 */
public class Market {
    ArrayList<TradeOffer> offeredTrades = new ArrayList<>();

    public Market() {

    }


    public ArrayList<TradeOffer> getOfferedTrades() {
        return offeredTrades;
    }

    public void clearOfferedTrades() {
        offeredTrades.clear();
    }

    public void addOfferedTrade(TradeOffer offer) {
        offeredTrades.add(offer);
    }

    public void addAllOffers(ArrayList<TradeOffer> offeredTrades) {
        this.offeredTrades.addAll(offeredTrades);
    }

    public void removeTradeOffer(TradeOffer offer) {
        System.out.println("Removing " + offer);
        offeredTrades.remove(offer);
    }

    public void removeAllTradeOffers(ArrayList<TradeOffer> offeredTrades) {
        offeredTrades.stream().map(t -> "Removing " + t).forEach(System.out::println);
        this.offeredTrades.removeAll(offeredTrades);
    }

    /**
     * Searches through the offered trades for the first credit that
     * passes cmp with true. Returns null if none pass cmp.
     * @param cmp A comparator function
     * @return credit or null
     */
    public Stream<TradeOffer> getOfferedTrades(Predicate<TradeOffer> cmp) {
        return offeredTrades.stream().filter(cmp);
    }

    /**
     * Searches through the offered trades for the first credit of item.
     * If none is found, null is returned.
     * @param item Item to be filtered
     * @return credit or null
     */
    public Stream<TradeOffer> getOfferedTrades(Item item) {
        return getOfferedTrades(offer -> offer.getItem() == item);
    }

    public Stream<TradeOffer> searchOfferedSellingTrades(Item item) {
        return getOfferedTrades(item).filter(TradeOffer::isSelling);
    }

    public Stream<TradeOffer> searchOfferedBuyingTrades(Item item) {
        return getOfferedTrades(item).filter(TradeOffer::isBuying);
    }

    public Optional<TradeOffer> searchBestSellingTrade(Item item) {
        return searchOfferedSellingTrades(item).min(Comparator.comparing(t -> t.getPrice().toDouble()));
    }

    public Optional<TradeOffer> searchBestBuyingTrade(Item item) {
        return searchOfferedBuyingTrades(item).max(Comparator.comparing(t -> t.getPrice().toDouble()));
    }

    public Stream<Item> searchTradedItems() {
        return offeredTrades.stream().map(TradeOffer::getItem).distinct();
    }

    public Stream<Item> searchSoldItems() {
        return offeredTrades.stream().filter(TradeOffer::isSelling).map(TradeOffer::getItem).distinct();
    }

    public Stream<Item> searchBoughtItems() {
        return offeredTrades.stream().filter(TradeOffer::isBuying).map(TradeOffer::getItem).distinct();
    }
}
