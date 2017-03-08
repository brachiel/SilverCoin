package ch.chrummibei.silvercoin.universe.entity_systems;

import ch.chrummibei.silvercoin.universe.components.PhysicsComponent;
import ch.chrummibei.silvercoin.universe.components.TradeSphereComponent;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import java.util.Optional;

/**
 * Created by brachiel on 26/02/2017.
 */
public class PhysicsSystem extends IteratingSystem implements ContactListener {
    class EntityContact {
        Entity a;
        Entity b;

        public EntityContact(Entity a, Entity b) {
            this.a = a;
            this.b = b;
        }
    }

    private static Family family = Family.all(PhysicsComponent.class).get();

    public PhysicsSystem() {
        super(family);
    }

    public PhysicsSystem(int priority) {
        super(family, priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        return; // Does nothing so far. If you do something here, you have to activate the system in Universe
    }

    public Optional<EntityContact> checkContact(Contact contact, ComponentMapper a, ComponentMapper b) {
        Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();

        if (a.has(entityA) && b.has(entityB)) return Optional.of(new EntityContact(entityA, entityB));
        if (a.has(entityB) && b.has(entityA)) return Optional.of(new EntityContact(entityB, entityA));
        return Optional.empty();
    }


    @Override
    public void beginContact(Contact contact) {
        checkContact(contact, Mappers.transport, Mappers.trader).ifPresent(ec ->
                TraderSystem.beginContactTransportTrader(ec.a, ec.b)
        );

        checkContact(contact, Mappers.tradeSphere, Mappers.tradeSphere).ifPresent(ec ->
                TradeSphereComponent.beginContactTradeSpheres(ec.a, ec.b)
        );
    }

    @Override
    public void endContact(Contact contact) {
        checkContact(contact, Mappers.tradeSphere, Mappers.tradeSphere).ifPresent(ec ->
                TradeSphereComponent.endContactTradeSpheres(ec.a, ec.b)
        );
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
