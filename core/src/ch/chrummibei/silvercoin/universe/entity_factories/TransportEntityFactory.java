package ch.chrummibei.silvercoin.universe.entity_factories;

import ch.chrummibei.silvercoin.constants.Categories;
import ch.chrummibei.silvercoin.universe.components.PathfinderComponent;
import ch.chrummibei.silvercoin.universe.components.PhysicsComponent;
import ch.chrummibei.silvercoin.universe.components.TransportComponent;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import ch.chrummibei.silvercoin.universe.trade.Trade;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;

/**
 * Created by brachiel on 21/02/2017.
 */
public class TransportEntityFactory {
    public static Entity Transport(Trade trade) {
        Entity entity = new Entity();
        PhysicsComponent sellerPhysics = Mappers.physics.get(trade.getSeller());
        PhysicsComponent buyerPhysics = Mappers.physics.get(trade.getBuyer());

        entity.add(new TransportComponent(trade));


        Filter filter = new Filter();
        filter.categoryBits = Categories.TRANSPORT;
        filter.maskBits = Categories.FACTORY | Categories.SHIP;
        entity.add(new PhysicsComponent(entity, sellerPhysics.body.getPosition(), BodyDef.BodyType.DynamicBody, 2, filter));
        entity.add(new PathfinderComponent(buyerPhysics.body.getPosition()));

        return entity;
    }

    public static void destroy(Entity entity) {
        PhysicsComponent physics = Mappers.physics.get(entity);
        physics.destroy();
        entity.removeAll();
    }
}
