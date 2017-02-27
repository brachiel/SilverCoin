package ch.chrummibei.silvercoin.universe.entity_factories;

import ch.chrummibei.silvercoin.universe.components.MarketComponent;
import ch.chrummibei.silvercoin.universe.components.PhysicsComponent;
import ch.chrummibei.silvercoin.universe.entity_systems.PhysicsSystem;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by brachiel on 26/02/2017.
 */
public class MarketEntityFactory {
    public static Entity Market(World box2dWorld, Vector2 position) {
        Entity entity = new Entity();
        entity.add(new MarketComponent());
        PhysicsComponent physics = new PhysicsComponent();
        entity.add(physics);

        PhysicsSystem.createBody(entity, box2dWorld, position, BodyDef.BodyType.StaticBody);

        CircleShape circle = new CircleShape();
        circle.setRadius(20f); // 20 meters

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1.5f;
        fixtureDef.friction = 0.01f;
        fixtureDef.restitution = 0.1f;

        physics.body.createFixture(fixtureDef);

        physics.body.setAngularDamping(2f);

        circle.dispose();

        return entity;
    }
}
