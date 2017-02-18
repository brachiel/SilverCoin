package ch.chrummibei.silvercoin.universe.trade;

import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.credit.TotalValue;

/**
 * An Arbitrage is a pair of profitable credit offers.
 */
public class Arbitrage {
    private TradeOffer buy;  // What we buy
    private TradeOffer sell; // What we sell

    private int tradableAmount;
    private Price priceDifference;
    private TotalValue projectedProfit;

    public Arbitrage(TradeOffer buy, TradeOffer sell) {
        if (buy.getItem() != sell.getItem()) {
            throw new IllegalArgumentException("Arbitrages have to be between the same item.");
        }

        this.buy = buy;
        this.sell = sell;

        tradableAmount = calcTradableAmount();
        priceDifference = calcPriceDifference();
        projectedProfit = calcProjectedProfit();
    }

    private int calcTradableAmount() {
        return Math.min(buy.getAmount(), sell.getAmount());
    }

    private Price calcPriceDifference() {
        return sell.getPrice().subtract(buy.getPrice());
    }

    private TotalValue calcProjectedProfit() {
        return calcPriceDifference().toTotalValue(calcTradableAmount());
    }

    public TradeOffer getBuy() {
        return buy;
    }

    public TradeOffer getSell() {
        return sell;
    }

    public int getTradableAmount() {
        return tradableAmount;
    }

    public Price getPriceDifference() {
        return priceDifference;
    }

    public TotalValue getProjectedProfit() {
        return projectedProfit;
    }

    public String toString() {
        return "Arbitrage of " + tradableAmount + " of " + buy.getItem() + " for a profit of " + projectedProfit;
    }
}
