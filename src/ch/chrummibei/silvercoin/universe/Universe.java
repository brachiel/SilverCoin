package ch.chrummibei.silvercoin.universe;

import ch.chrummibei.silvercoin.config.UniverseConfig;
import ch.chrummibei.silvercoin.universe.actor.Actor;
import ch.chrummibei.silvercoin.universe.actor.ArbitrageTradeActor;
import ch.chrummibei.silvercoin.universe.actor.BigSpenderActor;
import ch.chrummibei.silvercoin.universe.actor.FactoryActor;
import ch.chrummibei.silvercoin.universe.credit.Credit;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.position.PricedItemPosition;
import ch.chrummibei.silvercoin.universe.trade.Market;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import ch.chrummibei.silvercoin.universe.trade.Trader;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

/**
 * The universe contains the world, all actors and manages time and randomness. It is used by the Component to
 * tell the Screen what to render. All simulation is done here.
 */
public class Universe implements Actor {
    private static final Random random = new Random();

    private final UniverseConfig universeConfig;
    private final ArrayList<Item> catalogue = new ArrayList<>();
    private final Market market = new Market();
    private final ArrayList<Actor> actors = new ArrayList<>();
    private final ArrayList<FactoryActor> factories = new ArrayList<>();

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

    public void addActor(Actor actor) {
        actors.add(actor);
    }

    @Override
    public void tick(long timeDiffMillis) {
        actors.forEach(a -> a.tick(timeDiffMillis));
    }

    public void printStatus() {
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
        catalogue.addAll(universeConfig.item().getItems());

        // Create random traders
        for (int i = 0; i < 5; ++ i) {
            Trader trader = new Trader();
            trader.offerTradesAt(market);

            for (int j = 0, maxJ = getRandomInt(1,3); j < maxJ; ++j) {
                Item item = catalogue.get(random.nextInt(catalogue.size()));
                TradeOffer offer = new TradeOffer(trader, item, TradeOffer.TYPE.SELLING, getRandomInt(1,10), new Price(getRandomDouble(10,90)));
                trader.addToInventory(new PricedItemPosition(offer.getItem(), offer.getAmount(), offer.getTotalValue()));
                trader.addTradeOffer(offer);
            }

            for (int j = 0, maxJ = getRandomInt(1,3); j < maxJ; ++j) {
                Item item = catalogue.get(random.nextInt(catalogue.size()));
                TradeOffer offer = new TradeOffer(trader,item,TradeOffer.TYPE.BUYING, getRandomInt(1,10), new Price(getRandomDouble(10,90)));
                trader.addCredits(offer.getTotalValue());
                trader.addTradeOffer(offer);
            }
        }

        // Create 1 to 3 factories for every recipe with a goal stock of 5 to 15 items
        universeConfig.item().getRecipes().stream()
                .flatMap(recipe -> Stream.iterate(recipe,r -> r)
                                         .limit(universeConfig.factory().getRandomisedIntSetting("factoriesPerRecipe")))
                .forEach(recipe -> {
            FactoryActor factory = new FactoryActor(recipe, universeConfig.factory().getRandomisedIntSetting("goalStock"));
            factory.setSpread(universeConfig.factory().getRandomisedDoubleSetting("spreadFactor"));
            factory.addCredits(new Credit(universeConfig.factory().getRandomisedDoubleSetting("startingCredit")));
            factory.offerTradesAt(market);
            factory.adaptPricesFor(market);

            recipe.ingredients.keySet().forEach(ingredient -> {
                int startingAmount = (int) Math.round(factory.calcWantedAmountOfIngredient(ingredient)
                        * universeConfig.factory().getRandomisedDoubleSetting("inventoryPerIngredient"));
                Price purchasePrice = new Price(universeConfig.factory().getRandomisedDoubleSetting("purchasePricePerIngredient"));
                factory.addToInventory(new PricedItemPosition(ingredient, startingAmount, purchasePrice));
            });

            factories.add(factory);
        });
        factories.forEach(this::addActor);

        ArbitrageTradeActor arbitrageTradeActor = new ArbitrageTradeActor(market);
        addActor(arbitrageTradeActor);

        catalogue.stream().forEach(item -> System.out.println(item.getName()));
        Item transportShipItem = catalogue.stream().filter(item -> item.getName().equals("Transport ship")).findFirst().get();
        BigSpenderActor bigSpender = new BigSpenderActor(transportShipItem, 2000);
        bigSpender.offerTradesAt(market);
        addActor(bigSpender);
    }

    // Return a stream of all markets
    public Stream<Market> getMarkets() {
        return Stream.of(market);
    }

    public ArrayList<FactoryActor> getFactories() {
        return factories;
    }
}
