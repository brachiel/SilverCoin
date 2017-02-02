package ch.chrummibei.silvercoin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

public class Main {

    public static void main(String[] args) {
        Random random = new Random();
	    ArrayList<Item> catalogue = new ArrayList<>();
        Market market = new Market();

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
                trader.addOfferedTrade(offer);
	        }

            for (int j = 0; j < random.nextInt(15); ++j) {
                Item item = catalogue.get(random.nextInt(catalogue.size()));
                TradeOffer offer = new TradeOffer(trader,item,TradeOffer.TYPE.BUYING,random.nextInt(100), new Price(100*random.nextDouble()));
                trader.addOfferedTrade(offer);
	        }

            market.addAllOffers(trader);
        }

        catalogue.stream().filter(i -> i instanceof CraftableItem).map(CraftableItem.class::cast).forEachOrdered(i -> System.out.println(i.getIngredientString()));

        catalogue.forEach(item -> {
                Optional<TradeOffer> bestBuy = market.searchBestBuyingTrade(item);
                Optional<TradeOffer> bestSell = market.searchBestSellingTrade(item);
                System.out.println(
                        item + " " +
                        bestBuy.map(TradeOffer::compactString).orElse("No Buyer") + " " +
                        bestSell.map(TradeOffer::compactString).orElse("No Seller"));

        });

        ArbitrageTrader.findArbitrages(market).forEach(System.out::println);
    }
}


