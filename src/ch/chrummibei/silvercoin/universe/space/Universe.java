package ch.chrummibei.silvercoin.universe.space;

import ch.chrummibei.silvercoin.universe.actor.Actor;
import ch.chrummibei.silvercoin.universe.actor.ArbitrageTradeActor;
import ch.chrummibei.silvercoin.universe.actor.FactoryActor;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.item.CraftableItem;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.item.Recipe;
import ch.chrummibei.silvercoin.universe.position.PricedItemPosition;
import ch.chrummibei.silvercoin.universe.trade.Market;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import ch.chrummibei.silvercoin.universe.trade.Trader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Created by brachiel on 03/02/2017.
 */
public class Universe implements Actor {
    private Random random = new Random();
    private ArrayList<Item> catalogue = new ArrayList<>();
    private Market market = new Market();
    private ArrayList<Actor> actors = new ArrayList<>();
    private ArrayList<ArbitrageTradeActor> arbitrageTraders = new ArrayList<>();
    private ArrayList<FactoryActor> factories = new ArrayList<>();

    public Universe() {
        initialise();
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
        for (int i = 0; i < 10; ++i) {
            catalogue.add(new Item("Item " + i));
        }

        // Create 10 craftable items with random ingredients from catalogue
        for (int i = 0; i < 10; ++i) {
            CraftableItem item = new CraftableItem("CraftableItem " + i);
            HashMap<Item,Integer> ingredients = new HashMap<>();
            random.ints(random.nextInt(10),2,catalogue.size())
                    .forEach(j -> ingredients.put(catalogue.get(j), random.nextInt(10)));
            Recipe recipe = new Recipe(item, ingredients);
            item.addRecipe(recipe);
            catalogue.add(item);
        }

        for (int i = 0; i < 30; ++ i) {
            Trader trader = new Trader();

            for (int j = 0; j < random.nextInt(15); ++j) {
                Item item = catalogue.get(random.nextInt(catalogue.size()));
                TradeOffer offer = new TradeOffer(trader, item, TradeOffer.TYPE.SELLING, random.nextInt(100), new Price(100 * random.nextDouble()));
                trader.addToInventory(new PricedItemPosition(offer.getItem(), offer.getAmount(), offer.getTotalValue()));
                trader.addOfferedTrade(offer);
            }

            for (int j = 0; j < random.nextInt(15); ++j) {
                Item item = catalogue.get(random.nextInt(catalogue.size()));
                TradeOffer offer = new TradeOffer(trader,item,TradeOffer.TYPE.BUYING,random.nextInt(100), new Price(100*random.nextDouble()));
                trader.addCredits(offer.getTotalValue());
                trader.addOfferedTrade(offer);
            }

            trader.offerTradesAt(market);
        }

        arbitrageTraders.add(new ArbitrageTradeActor(market));
        arbitrageTraders.add(new ArbitrageTradeActor(market));
        arbitrageTraders.add(new ArbitrageTradeActor(market));
        arbitrageTraders.add(new ArbitrageTradeActor(market));
        arbitrageTraders.forEach(this::addActor);

        //catalogue.stream().filter(i -> i instanceof CraftableItem).map(CraftableItem.class::cast).forEachOrdered(i -> System.out.println(i.getIngredientString()));
    }

    // Return a stream of all markets
    public Stream<Market> getMarkets() {
        return Stream.of(market);
    }
}
