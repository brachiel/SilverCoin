package ch.chrummibei.silvercoin.universe;

import ch.chrummibei.silvercoin.config.UniverseConfig;
import ch.chrummibei.silvercoin.universe.components.MarketComponent;
import ch.chrummibei.silvercoin.universe.entity_factories.BigSpenderEntityFactory;
import ch.chrummibei.silvercoin.universe.entity_factories.FactoryEntityFactory;
import ch.chrummibei.silvercoin.universe.entity_systems.*;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

/**
 * The universe contains the world, all entities and manages randomness.
 */
public class Universe {
    private static final Random random = new Random();

    private final UniverseConfig universeConfig;
    private final ArrayList<Item> catalogue = new ArrayList<>();
    private final MarketComponent market = new MarketComponent();
    private final ArrayList<Entity> factories = new ArrayList<>();

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

    public void printStatus() {
        catalogue.forEach(item -> {
            Optional<TradeOffer> bestBuy = MarketUtil.searchBestBuyingTrade(market, item);
            Optional<TradeOffer> bestSell = MarketUtil.searchBestSellingTrade(market, item);
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
    }

    private void generateEntitySystems() {
        engine.addSystem(new TraderSystem(1));
        engine.addSystem(new FactorySystem(2));
        engine.addSystem(new BigSpenderSystem(3));
        engine.addSystem(new AISystem(4));
    }

    void generateEntities() {
        // Create random traders
        /*
        for (int i = 0; i < 5; ++ i) {
            Entity entity = TraderEntityFactory.RandomisedTraderEntity(catalogue, market);
            engine.addEntity(entity);
        }
        */

        // Create 1 to 3 factories for every recipe with a goal stock of 5 to 15 positions
        universeConfig.recipeBook().getRecipes().stream()
                .flatMap(recipe -> Stream.iterate(recipe,r -> r)
                                         .limit(universeConfig.factory().getRandomInt("factoriesPerRecipe")))
                .forEach(recipe -> {
            Entity entity = FactoryEntityFactory.FactoryEntity(universeConfig, market, recipe);
            factories.add(entity);
            engine.addEntity(entity);
        });

        /* TODO: Rewrite ArbitrageTrader as ComponentSystem
        ArbitrageTradeActor arbitrageTradeActor = new ArbitrageTradeActor(market);
        addActor(arbitrageTradeActor);
        */

        Item transportShipItem = catalogue.stream().filter(item -> item.getName().equals("Transport ship")).findFirst().get();
        Entity entity = BigSpenderEntityFactory.BigSpender(transportShipItem, market);
        engine.addEntity(entity);
    }

    public Stream<MarketComponent> getMarketComponents() {
        return Stream.of(market);
    }

    public ArrayList<Entity> getFactories() {
        return factories;
    }

    public void update(float delta) {
        System.out.println("---- TICK ----------------------------------------------------------");
        engine.update(delta);
    }
}
