package ch.chrummibei.silvercoin.universe.trade;

import ch.chrummibei.silvercoin.universe.components.TraderComponent;
import ch.chrummibei.silvercoin.universe.credit.TotalValue;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.position.PricedItemPosition;

/**
 * Trade containing amount and price of item to be traded.
 */
public class Trade {
    private final Item item;
    private final int amount;
    private final TotalValue totalValue;
    private TraderComponent buyer = null;
    private TraderComponent seller = null;
    public TradeOffer fromTradeOffer;

    public String toString() {
        return "Trade: " + amount + " " + item + ": " + seller + " -> " + buyer + " for " + totalValue;
    }

    public Trade(TradeOffer tradeOffer, TraderComponent acceptingTrader, int acceptingAmount) {
        if (acceptingAmount == 0) throw new RuntimeException("Trading amount=0. This is a bug.");

        if (tradeOffer.getType() == TradeOffer.TYPE.BUYING) {
            this.seller = acceptingTrader;
            this.buyer = tradeOffer.getOfferingTrader();
        } else {
            this.buyer = acceptingTrader;
            this.seller = tradeOffer.getOfferingTrader();
        }

        this.item = tradeOffer.getItem();
        this.amount = acceptingAmount;
        this.totalValue = new TotalValue(acceptingAmount * tradeOffer.getPrice().toDouble());
    }

    public Item getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public Double getPrice() {
        return totalValue.toDouble() / amount;
    }
    public Double getTotalValue() {
        return totalValue.toDouble();
    }

    public TraderComponent getBuyer() {
        return buyer;
    }

    public TraderComponent getSeller() {
        return seller;
    }

    public PricedItemPosition getTradersItemPosition(TraderComponent trader) throws TraderNotInvolvedException {
        if (amount == 0) {
            throw new RuntimeException("Trading item positions with amount = 0 doesn't make sense. This is a bug.");
        }

        if (trader == buyer) {
            return new PricedItemPosition(item, amount, totalValue);
        } else if (trader == seller) {
            return new PricedItemPosition(item, -amount, totalValue.invert());
        } else {
            throw new TraderNotInvolvedException();
        }
    }
}
