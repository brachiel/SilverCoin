package ch.chrummibei.silvercoin.universe.entity_systems;

import ch.chrummibei.silvercoin.messages.Messages;
import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.components.*;
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

    private void beginContactTraderMarket(Entity trader, Entity market) {
        // Add trader to be present at market.
        trader.add(new MarketAccessComponent(market));

        if (trader == Universe.player) {
            Universe.messageDispatcher.dispatchMessage(Messages.PLAYER_JOINED_MARKET, market);
        }
    }

    private void endContactTraderMarket(Entity trader, Entity market) {
        // Add trader to be present at market.
        trader.remove(MarketComponent.class);

        if (trader == Universe.player) {
            Universe.messageDispatcher.dispatchMessage(Messages.PLAYER_LEFT_MARKET);
        }
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
        checkContact(contact, Mappers.trader, Mappers.market).ifPresent(ec ->
            beginContactTraderMarket(ec.a, ec.b)
        );
        checkContact(contact, Mappers.transport, Mappers.trader).ifPresent(ec ->
            beginContactTransportTrader(ec.a, ec.b)
        );
    }

    private void beginContactTransportTrader(Entity transport, Entity trader) {
        TraderComponent traderComponent = Mappers.trader.get(trader);
        TransportComponent transportComponent = Mappers.transport.get(transport);

        // TODO: This doesn't belong here.
        if (TraderSystem.doesAcceptDelivery(trader, transport)) {
            TraderSystem.processDeliveredTrade(trader, transport);
        }
    }


    @Override
    public void endContact(Contact contact) {
        checkContact(contact, Mappers.trader, Mappers.market).ifPresent(ec ->
            endContactTraderMarket(ec.a, ec.b)
        );
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
