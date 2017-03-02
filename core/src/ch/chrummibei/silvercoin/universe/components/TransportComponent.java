package ch.chrummibei.silvercoin.universe.components;

import ch.chrummibei.silvercoin.universe.trade.Trade;
import com.badlogic.ashley.core.Component;

/**
 * Created by brachiel on 27/02/2017.
 */
public class TransportComponent implements Component {
    public Trade trade;

    public TransportComponent(Trade trade) {
        this.trade = trade;
    }
}
