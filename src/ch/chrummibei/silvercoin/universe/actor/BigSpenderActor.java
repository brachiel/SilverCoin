package ch.chrummibei.silvercoin.universe.actor;

import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.trade.Market;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import ch.chrummibei.silvercoin.universe.trade.TradeOfferHasNotEnoughAmountLeft;

import java.util.Optional;

/**
 * Item sink. Buys an item every consumptionTimeMillis milliseconds and pays with credits it creates out of thin air.
 */
public class BigSpenderActor extends TimeStepTraderActor {
    private final Item itemToConsume;
    private final long consumptionTimeMillis;

    public BigSpenderActor(Item itemToConsume, long consumptionTimeMillis) {
        super();
        this.itemToConsume = itemToConsume;
        this.consumptionTimeMillis = consumptionTimeMillis;
        addAction(this::buyItemIfAvailable, consumptionTimeMillis);
    }

    public void buyItemIfAvailable(long timeDiffMillis) {
        for (Market market : offersPresentAtMarkets) {
            Optional<TradeOffer> tradeOffer = market.searchBestSellingTrade(itemToConsume);
            if (! tradeOffer.isPresent()) {
                System.out.println("Big spender "+ this.getName() +" didn't find a trade for " + itemToConsume);
                continue;
            }

            try {
                System.out.println("Big spender "+ this.getName() +" is buying 1 " + itemToConsume);
                addCredits(tradeOffer.get().getPrice().toTotalValue(1)); // Give spender money for this trade.
                tradeOffer.get().accept(this, 1);
            } catch (TradeOfferHasNotEnoughAmountLeft tradeOfferHasNotEnoughAmountLeft) {
                tradeOfferHasNotEnoughAmountLeft.printStackTrace();
            }
        }
    }
}
