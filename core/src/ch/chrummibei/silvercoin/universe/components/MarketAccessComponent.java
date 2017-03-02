package ch.chrummibei.silvercoin.universe.components;

import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

/**
 * Created by brachiel on 20/02/2017.
 */
public class MarketAccessComponent implements Component {
    public Entity market;

    public MarketAccessComponent(Entity market) {
        this.market = market;
    }

    public MarketComponent getMarket() {
        return Mappers.market.get(market);
    }
}
