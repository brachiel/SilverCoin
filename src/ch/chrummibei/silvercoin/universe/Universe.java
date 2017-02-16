package ch.chrummibei.silvercoin.universe;

import ch.chrummibei.silvercoin.config.UniverseConfig;
import ch.chrummibei.silvercoin.universe.actor.Actor;
import ch.chrummibei.silvercoin.universe.actor.ArbitrageTradeActor;
import ch.chrummibei.silvercoin.universe.actor.FactoryActor;
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
 * Created by brachiel on 03/02/2017.
 */
public class Universe implements Actor {
    private static final Random random = new Random();

    private final UniverseConfig universeConfig;
    private ArrayList<Item> catalogue = new ArrayList<>();
    private Market market = new Market();
    private ArrayList<Actor> actors = new ArrayList<>();
    private ArrayList<ArbitrageTradeActor> arbitrageTraders = new ArrayList<>();
    private ArrayList<FactoryActor> factories = new ArrayList<>();

    public Universe(UniverseConfig universeConfig) {
        this.universeConfig = universeConfig;
        initialise();
    }

    public static int getRandomInt(int lowBound, int highBound) {
        return lowBound + random.nextInt(highBound - lowBound + 1); // +1 since upper bound is exclusive
    }

    public void addActor(Actor actor) {
        actors.add(actor);
    }

    @Override
    public void tick(long timeDiffMillis) {
        System.out.println("tick");
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
        catalogue = universeConfig.getItems();

        // Create random traders
        for (int i = 0; i < 30; ++ i) {
            Trader trader = new Trader();

            for (int j = 0; j < random.nextInt(15); ++j) {
                Item item = catalogue.get(random.nextInt(catalogue.size()));
                TradeOffer offer = new TradeOffer(trader, item, TradeOffer.TYPE.SELLING, random.nextInt(100), new Price(100 * random.nextDouble()));
                trader.addToInventory(new PricedItemPosition(offer.getItem(), offer.getAmount(), offer.getTotalValue()));
                trader.addTradeOffer(offer);
            }

            for (int j = 0; j < random.nextInt(15); ++j) {
                Item item = catalogue.get(random.nextInt(catalogue.size()));
                TradeOffer offer = new TradeOffer(trader,item,TradeOffer.TYPE.BUYING,random.nextInt(100), new Price(100*random.nextDouble()));
                trader.addCredits(offer.getTotalValue());
                trader.addTradeOffer(offer);
            }

            trader.offerTradesAt(market);
        }

        arbitrageTraders.add(new ArbitrageTradeActor(market));
        arbitrageTraders.add(new ArbitrageTradeActor(market));
        arbitrageTraders.add(new ArbitrageTradeActor(market));
        arbitrageTraders.add(new ArbitrageTradeActor(market));
        arbitrageTraders.forEach(this::addActor);

        // Create 1 to 3 factories for every recipe with a goal stock of 5 to 15 items
        universeConfig.getRecipes().stream().flatMap(recipe -> Stream.iterate(recipe,r -> r).limit(1+random.nextInt(2))).forEach(recipe -> {
            FactoryActor factory = new FactoryActor(recipe, 5+random.nextInt(10));
            factory.offerTradesAt(market);
            factory.adaptPricesFor(market);

            factories.add(factory);
        });
        factories.forEach(this::addActor);
    }

    // Return a stream of all markets
    public Stream<Market> getMarkets() {
        return Stream.of(market);
    }

    public ArrayList<FactoryActor> getFactories() {
        return factories;
    }
}
