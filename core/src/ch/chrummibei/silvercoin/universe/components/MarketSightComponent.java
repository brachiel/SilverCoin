package ch.chrummibei.silvercoin.universe.components;

import com.badlogic.ashley.core.Component;

import java.util.HashSet;

/**
 * Created by brachiel on 20/02/2017.
 */
public class MarketSightComponent implements Component {
    public HashSet<MarketComponent> markets = new HashSet<>();

    public MarketSightComponent(MarketComponent market) {
        markets.add(market);
    }
}
