package ch.chrummibei.silvercoin.universe.components;

import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.trade.Trade;
import ch.chrummibei.silvercoin.universe.trade.TradeNeed;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.ashley.core.Component;

import java.util.HashSet;
import java.util.stream.Stream;

/**
 * Created by brachiel on 20/02/2017.
 */
public class TraderComponent implements Component {
    public HashSet<Trade> acceptedTrades = new HashSet<>();
    public HashSet<TradeOffer> tradeOffers = new HashSet<>();
    public HashSet<TradeNeed> tradeNeeds = new HashSet<>();

    public Stream<TradeOffer> getTradeOffers() {
        return tradeOffers.stream();
    }

    public Stream<TradeOffer> filterTradeOffers(Item item, TradeOffer.TYPE type) {
        return tradeOffers.stream().filter(offer -> offer.getItem() == item && offer.getType() == type);
    }

    public void addTradeNeed(TradeNeed tradeNeed) {
        // Unify all existing needs for this item and delete them.
        tradeNeeds.removeIf(need -> {
                if (need.item != tradeNeed.item || need.type != tradeNeed.type) return false;
                tradeNeed.unify(need);
                return true;
        });
        tradeNeeds.add(tradeNeed);
    }

    public void removeTradeNeed(Item item, TradeOffer.TYPE type) {
        tradeNeeds.removeIf(need -> need.item == item && need.type == type);
    }
}
