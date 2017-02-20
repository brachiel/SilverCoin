package ch.chrummibei.silvercoin.universe.actor;

import ch.chrummibei.silvercoin.universe.entity_systems.MarketUtil;
import ch.chrummibei.silvercoin.universe.item.Item;
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
        for (MarketUtil market : offersPresentAtMarkets) {
            Optional<TradeOffer> tradeOffer = market.searchBestSellingTrade(itemToConsume);
            if (! tradeOffer.isPresent()) {
                continue;
            }

            try {
                addCredits(tradeOffer.get().getPrice().toTotalValue(1)); // Give spender money for this trade.
                tradeOffer.get().accept(this, 1);
            } catch (TradeOfferHasNotEnoughAmountLeft tradeOfferHasNotEnoughAmountLeft) {
                tradeOfferHasNotEnoughAmountLeft.printStackTrace();
            }
        }
    }
}
