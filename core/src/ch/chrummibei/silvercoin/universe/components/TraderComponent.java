package ch.chrummibei.silvercoin.universe.components;

import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.trade.Trade;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.ashley.core.Component;

import java.util.HashSet;

/**
 * Created by brachiel on 20/02/2017.
 */
public class TraderComponent implements Component {
    public HashSet<Trade> acceptedTrades = new HashSet<>();
    public HashSet<TradeOffer> ownTradeOffers = new HashSet<>();
    public HashSet<Item> wantsToBuy = new HashSet<>();
    public HashSet<Item> wantsToSell = new HashSet<>();
}
