package ch.chrummibei.silvercoin.universe.entity_factories;

import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.components.*;
import ch.chrummibei.silvercoin.universe.item.Item;
import com.badlogic.ashley.core.Entity;

/**
 * Created by brachiel on 21/02/2017.
 */
public class BigSpenderEntityFactory {
    private static int spenderNameSequence = 0;

    public static Entity BigSpender(Item itemToBuy, MarketComponent market) {
        Entity entity = new Entity();
        entity.add(new BigSpenderComponent(itemToBuy, (float) Universe.getRandomDouble(2,3)));
        entity.add(new MarketSightComponent(market));
        entity.add(new InventoryComponent());
        entity.add(new WalletComponent(0));
        entity.add(new TraderComponent());
        entity.add(new NamedComponent("Big Spender (" + itemToBuy + ") " + ++spenderNameSequence));
        return entity;
    }
}
