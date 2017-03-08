package ch.chrummibei.silvercoin.universe.entity_factories;

import ch.chrummibei.silvercoin.constants.Categories;
import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.components.*;
import ch.chrummibei.silvercoin.universe.item.Item;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;

/**
 * Created by brachiel on 21/02/2017.
 */
public class BigSpenderEntityFactory {
    private static int spenderNameSequence = 0;

    public static Entity BigSpender(Item itemToBuy, Vector2 position) {
        Entity entity = new Entity();
        entity.add(new BigSpenderComponent(itemToBuy, (float) Universe.getRandomDouble(2,3)));
        entity.add(new InventoryComponent());
        entity.add(new WalletComponent(0));
        entity.add(new TraderComponent());
        entity.add(new NamedComponent("Big Spender (" + itemToBuy + ") " + ++spenderNameSequence));
        Filter filter = new Filter();
        filter.categoryBits = Categories.FACTORY;
        filter.maskBits = Categories.TRANSPORT | Categories.SHIP;
        PhysicsComponent physics = new PhysicsComponent(entity, position, BodyDef.BodyType.StaticBody, 3, filter);
        entity.add(physics);
        entity.add(new TradeSphereComponent(physics, 200));

        return entity;
    }
}
