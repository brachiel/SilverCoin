package ch.chrummibei.silvercoin.universe.entity_factories;

import ch.chrummibei.silvercoin.universe.components.*;
import ch.chrummibei.silvercoin.universe.credit.TotalValue;
import ch.chrummibei.silvercoin.universe.entity_systems.PhysicsSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by brachiel on 26/02/2017.
 */
public class PlayerEntityFactory {
    public static Entity Player(World box2dWorld, Vector2 position) {
        Entity entity = new Entity();
        entity.add(new WalletComponent(new TotalValue(1000)));
        entity.add(new InventoryComponent());
        entity.add(new TraderComponent());
        entity.add(new NamedComponent("Player"));
        PhysicsComponent physics = new PhysicsComponent();
        entity.add(physics);
        entity.add(new PathfinderComponent());

        PhysicsSystem.createBody(entity, box2dWorld, position);

        CircleShape circle = new CircleShape();
        circle.setRadius(3f); // 3 meters

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1.5f;
        fixtureDef.friction = 0.01f;
        fixtureDef.restitution = 0.1f;

        physics.body.createFixture(fixtureDef);

        physics.body.setAngularDamping(2f);

        return entity;
    }
}
