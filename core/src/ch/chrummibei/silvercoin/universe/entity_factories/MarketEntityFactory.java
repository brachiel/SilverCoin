package ch.chrummibei.silvercoin.universe.entity_factories;

import ch.chrummibei.silvercoin.universe.components.MarketComponent;
import ch.chrummibei.silvercoin.universe.components.PhysicsComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * Created by brachiel on 26/02/2017.
 */
public class MarketEntityFactory {
    public static Entity Market(Vector2 position) {
        Entity entity = new Entity();
        entity.add(new MarketComponent());

        CircleShape circle = new CircleShape();
        circle.setRadius(6f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 1.5f;
        fixtureDef.friction = 0.01f;
        fixtureDef.restitution = 0.1f;

        PhysicsComponent physics = new PhysicsComponent(entity, position, BodyDef.BodyType.StaticBody, fixtureDef);
        entity.add(physics);

        physics.body.setAngularDamping(2f);

        return entity;
    }
}
