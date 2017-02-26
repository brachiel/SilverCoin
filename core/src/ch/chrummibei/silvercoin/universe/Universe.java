package ch.chrummibei.silvercoin.universe;

import ch.chrummibei.silvercoin.config.UniverseConfig;
import ch.chrummibei.silvercoin.universe.components.MarketComponent;
import ch.chrummibei.silvercoin.universe.entity_factories.BigSpenderEntityFactory;
import ch.chrummibei.silvercoin.universe.entity_factories.FactoryEntityFactory;
import ch.chrummibei.silvercoin.universe.entity_factories.PlayerEntityFactory;
import ch.chrummibei.silvercoin.universe.entity_systems.*;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;

import java.util.*;
import java.util.stream.Stream;

/**
 * The universe contains the world, all entities and manages randomness.
 */
public class Universe {
    private static final Random random = new Random();
    public static final boolean DEBUG = false;

    private final UniverseConfig universeConfig;
    private final HashSet<Item> catalogue = new HashSet<>();
    private final HashSet<Entity> markets = new HashSet();
    private final HashSet<Entity> factories = new HashSet<>();
    public final World box2dWorld = new World(new Vector2(0,0), true);

    private final Engine engine = new Engine();

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

        generateEntities();
        generateEntitySystems();

        // Initialise physics system
        Box2D.init();
    }

    private void generateEntitySystems() {
        engine.addSystem(new TraderSystem(1));
        engine.addSystem(new FactorySystem(2));
        engine.addSystem(new BigSpenderSystem(3));
        engine.addSystem(new AISystem(4));
        engine.addSystem(new PlayerSystem(5));
    }

    void generateEntities() {

        Entity player = PlayerEntityFactory.Player(box2dWorld, new Vector2(100, 100));
        add(player);

        // Add two markets
        markets.add(new Entity().add(new MarketComponent()));
        markets.add(new Entity().add(new MarketComponent()));


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
                        Mappers.market.get(market),
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
            Entity entity = BigSpenderEntityFactory.BigSpender(transportShipItem, Mappers.market.get(market));
            add(entity);
        });
    }

    public Stream<MarketComponent> getMarketComponents() {
        return markets.stream().map(Mappers.market::get);
    }

    public HashSet<Entity> getFactories() {
        return factories;
    }

    public void update(float delta) {
        System.out.println("---- TICK ----------------------------------------------------------");

        // Game logic update
        engine.update(delta);

        // Physics simulation
        box2dWorld.step(delta, 6, 2);
    }

    public void add(Entity entity) {
        engine.addEntity(entity);
    }
}
