package ch.chrummibei.silvercoin.universe.components;

import ch.chrummibei.silvercoin.universe.Universe;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by brachiel on 26/02/2017.
 */
public class PhysicsComponent implements Component {
    public Fixture fixture;
    public BodyDef bodyDef;
    public Body body;

    public PhysicsComponent(Entity entity, Vector2 position, BodyDef.BodyType type) {
        this(entity, position, type, createCircleFixtureDef(3, null));
    }

    public PhysicsComponent(Entity entity, Vector2 position, BodyDef.BodyType type, float radius, Filter filter) {
        this(entity, position, type, createCircleFixtureDef(radius, filter));
    }

    public PhysicsComponent(Entity entity, Vector2 position, BodyDef.BodyType type, FixtureDef fixtureDef) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(position);

        this.body = Universe.box2dWorld.createBody(bodyDef);
        this.bodyDef = bodyDef;
        this.body.setUserData(entity);
        this.fixture = this.body.createFixture(fixtureDef);
        fixtureDef.shape.dispose();
    }

    public static FixtureDef createCircleFixtureDef(float radius, Filter filter) {
        CircleShape circle = new CircleShape();
        circle.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.friction = 0;
        if (filter != null) {
            fixtureDef.filter.categoryBits = filter.categoryBits;
            fixtureDef.filter.groupIndex = filter.groupIndex;
            fixtureDef.filter.maskBits = filter.maskBits;
        }

        return fixtureDef;
    }

    public void destroy() {
        // We should not remove bodies when the simulation is running.
        Universe.addBodyToDestroy(body);
        Universe.addFixtureToDestroy(body, fixture);

        body = null;
        bodyDef = null;
        fixture = null;
    }
}
