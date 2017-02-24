package ch.chrummibei.silvercoin.universe.entity_factories;

import ch.chrummibei.silvercoin.universe.components.*;
import ch.chrummibei.silvercoin.universe.item.Item;
import com.badlogic.ashley.core.Entity;

/**
 * Created by brachiel on 21/02/2017.
 */
public class BigSpenderEntityFactory {
    public static Entity BigSpender(Item itemToBuy, MarketComponent market) {
        Entity entity = new Entity();
        entity.add(new BigSpenderComponent(itemToBuy, 2000));
        entity.add(new MarketSightComponent(market));
        entity.add(new InventoryComponent());
        entity.add(new WalletComponent(0));
        entity.add(new TraderComponent());
        return entity;
    }
}
