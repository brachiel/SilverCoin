package ch.chrummibei.silvercoin.universe.components;

import ch.chrummibei.silvercoin.constants.Categories;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import java.util.HashSet;
import java.util.stream.Stream;

/**
 * Adds a fixture to the trader body that when it touches other trade spheres, lists their trades.
 * This allows traders to have a sphere around them in which they see other trades.
 */
public class TradeSphereComponent implements Component {
    public final Fixture tradeSphere;
    public final HashSet<Entity> tradersInSphere = new HashSet<>();

    public TradeSphereComponent(PhysicsComponent physics, float radius) {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.filter.categoryBits = Categories.TRADESPHERE;
        fixtureDef.filter.maskBits = Categories.TRADESPHERE;
        fixtureDef.friction = 0;
        fixtureDef.density = 0;
        fixtureDef.isSensor = true;

        tradeSphere = physics.body.createFixture(fixtureDef);

        circleShape.dispose();
    }

    public static void beginContactTradeSpheres(Entity entityA, Entity entityB) {
        Mappers.tradeSphere.get(entityA).tradersInSphere.add(entityB);
        Mappers.tradeSphere.get(entityB).tradersInSphere.add(entityA);
    }

    public static void endContactTradeSpheres(Entity entityA, Entity entityB) {
        Mappers.tradeSphere.get(entityA).tradersInSphere.remove(entityB);
        Mappers.tradeSphere.get(entityB).tradersInSphere.remove(entityA);
    }

    public Stream<TradeOffer> getAllTrades() {
        return tradersInSphere.stream()
                .map(Mappers.trader::get)
                .flatMap(TraderComponent::getTradeOffers);
    }
}
