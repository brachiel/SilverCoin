package ch.chrummibei.silvercoin;

import java.util.ArrayList;
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

    public void addAllOffers(Market market) {
        offeredTrades.addAll(market.getOfferedTrades());
    }

    /**
     * Searches through the offered trades for the first trade that
     * passes cmp with true. Returns null if none pass cmp.
     * @param cmp A comparator function
     * @return trade or null
     */
    public Stream<TradeOffer> searchOfferedTrades(Predicate<TradeOffer> cmp) {
        return offeredTrades.stream().filter(cmp);
    }

    /**
     * Searches through the offered trades for the first trade of item.
     * If none is found, null is returned.
     * @param item
     * @return trade or null
     */
    public Stream<TradeOffer> searchOfferedTrades(Item item) {
        return searchOfferedTrades(offer -> offer.getItem() == item);
    }

    public Stream<TradeOffer> searchOfferedSellingTrades(Item item) {
        return searchOfferedTrades(offer -> offer.getItem() == item && offer.isSelling());
    }

    public Stream<TradeOffer> searchOfferedBuyingTrades(Item item) {
        return searchOfferedTrades(offer -> offer.getItem() == item && offer.isBuying());
    }
}
