package ch.chrummibei.silvercoin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Random random = new Random();
	    ArrayList<Item> catalogue = new ArrayList<>();
        Market market = new Market();

	    for (int i = 0; i < 10; ++i) {
	        catalogue.add(new Item("Item " + i));
        }

        for (int i = 0; i < 30; ++ i) {
	        Trader trader = new Trader();

	        for (int j = 0; j < random.nextInt(15); ++j) {
                Item item = catalogue.get(random.nextInt(catalogue.size()));
                TradeOffer offer = new TradeOffer(trader, item, TradeOffer.SELLING, random.nextInt(100), 100 * random.nextDouble());
                trader.addOfferedTrade(offer);
	        }

            for (int j = 0; j < random.nextInt(15); ++j) {
                Item item = catalogue.get(random.nextInt(catalogue.size()));
                TradeOffer offer = new TradeOffer(trader,item,TradeOffer.BUYING,random.nextInt(100),100*random.nextDouble());
                trader.addOfferedTrade(offer);
	        }

            market.addAllOffers(trader);
        }

        market.searchOfferedBuyingTrades(catalogue.get(0))
                .sorted(Comparator.comparing(t -> t.getItem().getName()))
                .forEachOrdered(
                        (TradeOffer offer) -> System.out.println(offer)
                );
    }
}


