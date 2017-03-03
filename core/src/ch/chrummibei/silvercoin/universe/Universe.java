package ch.chrummibei.silvercoin.universe;

import ch.chrummibei.silvercoin.config.UniverseConfig;
import ch.chrummibei.silvercoin.universe.components.MarketComponent;
import ch.chrummibei.silvercoin.universe.entity_factories.BigSpenderEntityFactory;
import ch.chrummibei.silvercoin.universe.entity_factories.FactoryEntityFactory;
import ch.chrummibei.silvercoin.universe.entity_factories.MarketEntityFactory;
import ch.chrummibei.silvercoin.universe.entity_factories.PlayerEntityFactory;
import ch.chrummibei.silvercoin.universe.entity_systems.*;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.util.*;
import java.util.stream.Stream;

/**
 * The universe contains the world, all entities and manages randomness.
 */
public class Universe {
    public static final boolean DEBUG = false;
    public static final World box2dWorld = new World(new Vector2(0,0), true);

    private static final Random random = new Random();
    public static final MessageDispatcher messageDispatcher = new MessageDispatcher();
    public static final Engine engine = new Engine();
    private static final HashSet<Body> deadBodies = new HashSet<>();
    private static final HashMap<Body, Fixture> deadFixtures = new HashMap<>();

    public enum EVENTS { PLAYER_JOINED_MARKET, PLAYER_LEFT_MARKET }

    private final UniverseConfig universeConfig;
    private final HashSet<Item> catalogue = new HashSet<>();
    private final HashSet<Entity> markets = new HashSet();
    private final HashSet<Entity> factories = new HashSet<>();

    public static Entity player;

    public PlayerSystem playerSystem;
    public PhysicsSystem physicsSystem;

    public Universe(UniverseConfig universeConfig) {
        this.universeConfig = universeConfig;
        initialise();
    }

    public static int getRandomInt(int lowBound, int highBound) {
        return lowBound + random.nextInt(highBound - lowBound + 1); // +1 since upper bound is exclusive
    }

    public static double getRandomDouble(double lowBound, double highBound) {
        return lowBound + (highBound-lowBound)*random.nextDouble();
    }

    public static Vector2 getRandomPosition(Vector2 center, float dx, float dy) {
        return new Vector2(
                (float) getRandomDouble(center.x - dx, center.x + dx),
                (float) getRandomDouble(center.y - dy, center.y + dy));
    }

    public UniverseConfig getUniverseConfig() {
        return universeConfig;
    }

    public ArrayList<Item> getItems() {
        return universeConfig.catalogue().getItems();
    }

    public void printStatus(Entity entity) {
        MarketComponent market = Mappers.market.get(entity);

        catalogue.forEach(item -> {
            Optional<TradeOffer> bestBuy = market.searchBestBuyingTrade(item);
            Optional<TradeOffer> bestSell = market.searchBestSellingTrade(item);
            System.out.println(
                    item + " BUY: " +
                            bestBuy.map(TradeOffer::compactString).orElse("No Buyer") + " SELL: " +
                            bestSell.map(TradeOffer::compactString).orElse("No Seller"));

        });
    }

    void initialise() {
        catalogue.addAll(universeConfig.catalogue().getItems());

        // Turn on message debugger
        messageDispatcher.setDebugEnabled(false);

        generateEntities();
        generateEntitySystems();

        // Initialise physics system
        Box2D.init();

        // Have the physics system handle collisions
        box2dWorld.setContactListener(physicsSystem);
    }

    private void generateEntitySystems() {
        engine.addSystem(new TraderSystem(1));
        engine.addSystem(new FactorySystem(2));
        engine.addSystem(new BigSpenderSystem(3));
        engine.addSystem(new AISystem(4));
        physicsSystem = new PhysicsSystem(6);
        // engine.addSystem(physicsSystem); // The entity processor does nothing, so far.
        engine.addSystem(new PathfinderSystem(7));
        playerSystem = new PlayerSystem(8);
        engine.addSystem(playerSystem);
    }

    Vector2 findEmptySpot(float minDistance) {
        Array<Body> bodies = new Array<>();
        box2dWorld.getBodies(bodies);
        Vector2 newSpot = null;
        for (int tryNumber = 1; tryNumber < 10; ++tryNumber) {
            newSpot = getRandomPosition(new Vector2(400,200), 400-10, 200-10);
            if (hasEnoughSpace(newSpot, bodies, minDistance)) {
                return newSpot;
            }
        }
        return newSpot;
    }

    boolean hasEnoughSpace(Vector2 spot, Array<Body> bodies, float minDistance) {
        for (Body body : bodies) {
            if (spot.dst(body.getPosition()) < minDistance) return false;
        }
        return true;
    }

    void generateEntities() {
        player = PlayerEntityFactory.Player(box2dWorld, new Vector2(100, 100));
        add(player);

        // Add two markets
        markets.add(MarketEntityFactory.Market(new Vector2(300,200)));
        markets.add(MarketEntityFactory.Market(new Vector2(400,100)));

        universeConfig.recipeBook().getRecipes().stream()
            .flatMap(recipe -> Stream.iterate(recipe,r -> r)
                                     .limit(universeConfig.factory().getRandomInt("factoriesPerRecipe")))
            .forEach(recipe -> {
                // Choose random market
                int r = Universe.getRandomInt(0, markets.size()-1);
                Entity market = null;
                Iterator<Entity> marketIterator = markets.iterator();
                for (int i = 0; i <= r; ++i) {
                    market = marketIterator.next();
                }

                Entity entity = FactoryEntityFactory.FactoryEntity(
                        universeConfig,
                        market,
                        findEmptySpot(20),
                        recipe);
                factories.add(entity);
                add(entity);
        });

        /* TODO: Rewrite ArbitrageTrader as ComponentSystem
        ArbitrageTradeActor arbitrageTradeActor = new ArbitrageTradeActor(market);
        addActor(arbitrageTradeActor);
        */

        Item transportShipItem = catalogue.stream().filter(item -> item.getName().equals("Transport ship")).findFirst().get();
        markets.forEach(market -> {
            Entity entity = BigSpenderEntityFactory.BigSpender(
                    transportShipItem,
                    market,
                    getRandomPosition(new Vector2(400,200), 400, 200));
            add(entity);
        });
    }

    public Stream<Entity> getMarkets() {
        return markets.stream();
    }

    public HashSet<Entity> getFactories() {
        return factories;
    }

    public void update(float delta) {
        //System.out.println("---- TICK ----------------------------------------------------------");

        // Message update
        messageDispatcher.update();

        // Game logic update
        engine.update(delta);

        // Physics simulation
        box2dWorld.step(delta, 6, 2);

        // We're finished with the simulation. Now it is safe to delete bodies
        deadFixtures.forEach((body, fixture) -> body.destroyFixture(fixture));
        deadFixtures.clear();
        deadBodies.forEach(body -> box2dWorld.destroyBody(body));
        deadBodies.clear();
    }

    public void add(Entity entity) {
        engine.addEntity(entity);
    }
    
    public static void addBodyToDestroy(Body body) {
        deadBodies.add(body);
    }

    public static void addFixtureToDestroy(Body body, Fixture fixture) {
        deadFixtures.put(body, fixture);
    }
}
