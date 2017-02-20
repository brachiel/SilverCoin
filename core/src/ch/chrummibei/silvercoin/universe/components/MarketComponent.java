package ch.chrummibei.silvercoin.universe.components;

import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.ashley.core.Component;

import java.util.ArrayList;

/**
 * Created by brachiel on 20/02/2017.
 */
public class MarketComponent implements Component {
    public ArrayList<TradeOffer> offeredTrades = new ArrayList<>();
}
