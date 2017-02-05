package ch.chrummibei.silvercoin.space;

import ch.chrummibei.silvercoin.actor.Actor;
import ch.chrummibei.silvercoin.actor.ArbitrageTradeActor;
import ch.chrummibei.silvercoin.credit.Price;
import ch.chrummibei.silvercoin.item.CraftableItem;
import ch.chrummibei.silvercoin.item.Item;
import ch.chrummibei.silvercoin.position.PricedItemPosition;
import ch.chrummibei.silvercoin.trade.Market;
import ch.chrummibei.silvercoin.trade.TradeOffer;
import ch.chrummibei.silvercoin.trade.Trader;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

/**
 * Created by brachiel on 03/02/2017.
 */
public class Universe implements Actor {
    private boolean running = false;
    private Double targetTicksPerSecond = 10.0;
    private Random random = new Random();
    private ArrayList<Item> catalogue = new ArrayList<>();
    private Market market = new Market();
    private ArrayList<Actor> actors = new ArrayList<>();
    private ArrayList<ArbitrageTradeActor> arbitrageTraders = new ArrayList<>();

    public Universe() {
        initialise();
    }

    public void addActor(Actor actor) {
        System.out.println("Adding " + actor + " to Universe");
        actors.add(actor);
    }

    @Override
    public void tick(double timeDiff) {
        System.out.println("Tick " + timeDiff);
        actors.forEach(a -> a.tick(timeDiff));
        System.out.println();
        System.out.println();
        System.out.println();
        printStatus();
    }

    public void run() {
        long lastTickMillis = System.currentTimeMillis();
        long totalTicks = 0;
        running = true;

        while (running && totalTicks < 1000) {
            long nowMillis = System.currentTimeMillis();
            tick((nowMillis - lastTickMillis)*1000); // This might take a while

            lastTickMillis = System.currentTimeMillis();
            // We have to sleep currentTime + 1000/targetTicksPerSecond - now
            try {
                long millisToSleep = (long) (nowMillis + 1000/targetTicksPerSecond - lastTickMillis);
                System.out.println("Sleeping " + millisToSleep);
                if (millisToSleep > 0) {
                    Thread.sleep(millisToSleep);
                } else {
                    System.out.println("Trouble keeping up...");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                running = false;
            }

            ++totalTicks;
        }
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

    public void initialise() {

        for (int i = 0; i < 10; ++i) {
            catalogue.add(new Item("Item " + i));
        }

        // Create 10 craftable items with random ingredients from catalogue
        for (int i = 0; i < 10; ++i) {
            CraftableItem item = new CraftableItem("CraftableItem " + i);
            random.ints(random.nextInt(10),2,catalogue.size())
                    .forEach(j -> item.addIngredient(catalogue.get(j), random.nextInt(10)));
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
        arbitrageTraders.forEach(a -> this.addActor(a));

        catalogue.stream().filter(i -> i instanceof CraftableItem).map(CraftableItem.class::cast).forEachOrdered(i -> System.out.println(i.getIngredientString()));
    }
}
