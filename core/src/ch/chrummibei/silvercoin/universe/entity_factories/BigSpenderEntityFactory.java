package ch.chrummibei.silvercoin.universe.entity_factories;

import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.components.*;
import ch.chrummibei.silvercoin.universe.item.Item;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;

/**
 * Created by brachiel on 21/02/2017.
 */
public class BigSpenderEntityFactory {
    private static int spenderNameSequence = 0;

    public static Entity BigSpender(Item itemToBuy, Entity market, Vector2 position) {
        Entity entity = new Entity();
        entity.add(new BigSpenderComponent(itemToBuy, (float) Universe.getRandomDouble(2,3)));
        entity.add(new MarketAccessComponent(market));
        entity.add(new InventoryComponent());
        entity.add(new WalletComponent(0));
        entity.add(new TraderComponent());
        entity.add(new NamedComponent("Big Spender (" + itemToBuy + ") " + ++spenderNameSequence));
        entity.add(new PhysicsComponent(entity, position, BodyDef.BodyType.StaticBody));
        return entity;
    }
}
