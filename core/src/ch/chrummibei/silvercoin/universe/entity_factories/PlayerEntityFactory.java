package ch.chrummibei.silvercoin.universe.entity_factories;

import ch.chrummibei.silvercoin.universe.components.*;
import ch.chrummibei.silvercoin.universe.credit.TotalValue;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * Created by brachiel on 26/02/2017.
 */
public class PlayerEntityFactory {
    public static Entity Player(Vector2 position) {
        Entity entity = new Entity();
        entity.add(new PlayerComponent());
        entity.add(new WalletComponent(new TotalValue(1000)));
        entity.add(new InventoryComponent());
        entity.add(new TraderComponent());
        entity.add(new NamedComponent("Player"));

        CircleShape circle = new CircleShape();
        circle.setRadius(3f); // 3 meters

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1.5f;
        fixtureDef.friction = 0.01f;
        fixtureDef.restitution = 0.1f;
        PhysicsComponent physics = new PhysicsComponent(entity, position, BodyDef.BodyType.DynamicBody, fixtureDef);
        entity.add(physics);
        entity.add(new PathfinderComponent(null));

        physics.body.setAngularDamping(2f);

        entity.add(new TradeSphereComponent(physics, 200));

        return entity;
    }
}
