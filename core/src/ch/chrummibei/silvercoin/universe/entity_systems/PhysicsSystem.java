package ch.chrummibei.silvercoin.universe.entity_systems;

import ch.chrummibei.silvercoin.gui.SilverCoin;
import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.components.MarketComponent;
import ch.chrummibei.silvercoin.universe.components.PhysicsComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by brachiel on 26/02/2017.
 */
public class PhysicsSystem extends IteratingSystem implements ContactListener {
    private static Family family = Family.all(PhysicsComponent.class).get();

    public PhysicsSystem() {
        super(family);
    }

    public PhysicsSystem(int priority) {
        super(family, priority);
    }

    public static void createBody(Entity entity, World box2dWorld, Vector2 position, BodyDef.BodyType type) {
        PhysicsComponent physics = Mappers.physics.get(entity);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(position);

        physics.body = box2dWorld.createBody(bodyDef);
        physics.bodyDef = bodyDef;

        physics.body.setUserData(entity);
    }


    private void beginContactTraderMarket(Entity traderEntity, Entity marketEntity) {
        // Add trader to be present at market.
        MarketComponent market = Mappers.market.get(marketEntity);
        traderEntity.add(market);

        if (traderEntity == Universe.player) {
            SilverCoin.self.playerJoinedMarket(market);
        }
    }

    private void endContactTraderMarket(Entity traderEntity, Entity marketEntity) {
        // Add trader to be present at market.
        traderEntity.remove(MarketComponent.class);

        if (traderEntity == Universe.player) {
            SilverCoin.self.playerLeftMarket();
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        return; // Does nothing so far. If you do something here, you have to activate the system in Universe
    }


    @Override
    public void beginContact(Contact contact) {
        Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();

        if (Mappers.trader.has(entityA) && Mappers.market.has(entityB) && !Mappers.market.has(entityA)) {
            beginContactTraderMarket(entityA, entityB);
        } else if (Mappers.trader.has(entityB) && Mappers.market.has(entityA) && !Mappers.market.has(entityB)) {
            beginContactTraderMarket(entityB, entityA);
        }
    }


    @Override
    public void endContact(Contact contact) {
        Entity entityA = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity entityB = (Entity) contact.getFixtureB().getBody().getUserData();

        if (Mappers.trader.has(entityA) && Mappers.market.has(entityB) && !Mappers.market.has(entityA)) {
            endContactTraderMarket(entityA, entityB);
        } else if (Mappers.trader.has(entityB) && Mappers.market.has(entityA) && !Mappers.market.has(entityB)) {
            endContactTraderMarket(entityB, entityA);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
