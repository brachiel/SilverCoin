package ch.chrummibei.silvercoin.universe.trade;

import ch.chrummibei.silvercoin.universe.credit.TotalValue;
import ch.chrummibei.silvercoin.universe.item.Item;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A market is a list of TradeOffers that makes them comparable and searchable.
 */
public class Market {
    private final ArrayList<TradeOffer> offeredTrades = new ArrayList<>();

    public Market() {

    }


    public ArrayList<TradeOffer> searchOfferedTrades() {
        return offeredTrades;
    }

    public void clearOfferedTrades() {
        offeredTrades.clear();
    }

    public ArrayList<TradeOffer> getOfferedTrades() {
        return offeredTrades;
    }

    public void addTradeOffer(TradeOffer offer) {
        offeredTrades.add(offer);
    }

    public void addAllOffers(ArrayList<TradeOffer> offeredTrades) {
        this.offeredTrades.addAll(offeredTrades);
    }

    public void removeTradeOffer(TradeOffer offer) {
        offeredTrades.remove(offer);
    }

    public boolean hasTradeOffer(TradeOffer tradeOffer) {
        return offeredTrades.contains(tradeOffer);
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
    public Stream<TradeOffer> searchOfferedTrades(Predicate<TradeOffer> cmp) {
        return offeredTrades.stream().filter(cmp);
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
    public Stream<TradeOffer> searchOfferedTrades(Item item) {
        return searchOfferedTrades(offer -> offer.getItem() == item);
    }

    public Stream<TradeOffer> searchOfferedTrades(Item item, TradeOffer.TYPE type) {
        return searchOfferedTrades(offer -> offer.getItem() == item && offer.getType() == type);
    }

    public Stream<TradeOffer> searchOfferedSellingTrades(Item item) {
        return searchOfferedTrades(item).filter(TradeOffer::isSelling);
    }

    public Stream<TradeOffer> searchOfferedBuyingTrades(Item item) {
        return searchOfferedTrades(item).filter(TradeOffer::isBuying);
    }

    public Optional<TradeOffer> searchBestSellingTrade(Item item) {
        return searchOfferedSellingTrades(item).min(Comparator.comparing(t -> t.getPrice().toDouble()));
    }

    public Optional<TradeOffer> searchBestBuyingTrade(Item item) {
        return searchOfferedBuyingTrades(item).max(Comparator.comparing(t -> t.getPrice().toDouble()));
    }

    public Map<TradeOffer,Integer> getTradeOffersToTradeAmount(Item item, TradeOffer.TYPE type, int amount) {
        HashMap<TradeOffer,Integer> tradeOffers = new HashMap<>();

        int amountLeftToTrade = amount;
        List<TradeOffer> sortedTradeOffers = searchOfferedTrades(item, type)
                // If we're buying, we want to order priced ascending; if we're selling we want to sort them descending
                .sorted(bestPriceComparator())
                .collect(Collectors.toList());


        for (TradeOffer tradeOffer : sortedTradeOffers) {
            int amountToTradeWithThisOffer = Math.min(amountLeftToTrade, tradeOffer.getAmount());
            amountLeftToTrade -= amountToTradeWithThisOffer;

            tradeOffers.put(tradeOffer, amountToTradeWithThisOffer);

            if (amountLeftToTrade <= 0) {
                // Last Buy
                break;
            }
        }

        return tradeOffers;
    }

    public Optional<TotalValue> calculateTotalBuyCosts(Item item, int amount) {
        TotalValue totalValue = new TotalValue(0);

        for (Map.Entry<TradeOffer, Integer> entry : getTradeOffersToTradeAmount(item, TradeOffer.TYPE.BUYING, amount).entrySet()) {
            totalValue.iAdd(entry.getKey().getPrice().toTotalValue(entry.getValue()));
        }

        if (totalValue.toDouble() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(totalValue);
        }
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
