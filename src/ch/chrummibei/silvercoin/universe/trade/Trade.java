package ch.chrummibei.silvercoin.universe.trade;

import ch.chrummibei.silvercoin.universe.credit.TotalValue;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.position.PricedItemPosition;

/**
 * Trade containing amount and price of item to be traded.
 */
public class Trade {
    private Item item;
    private int amount;
    private TotalValue totalValue;
    private Trader buyer = null;
    private Trader seller = null;

    public String toString() {
        return "Trade: " + amount + " " + item + ": " + seller + " -> " + buyer + " for " + totalValue;
    }

    public Trade(Trader buyer, Trader seller, Item item, int amount, Price price) {
        this.seller = seller;
        this.buyer = buyer;

        this.item = item;
        this.amount = amount;
        this.totalValue = new TotalValue(amount * price.toDouble());
    }

    public Trade(Trader buyer, Trader seller, Item item, int amount, TotalValue totalValue) {
        this.seller = seller;
        this.buyer = buyer;

        this.item = item;
        this.amount = amount;
        this.totalValue = totalValue;
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

    public Trader getBuyer() {
        return buyer;
    }

    public Trader getSeller() {
        return seller;
    }

    public PricedItemPosition getTradersItemPosition(Trader trader) throws TraderNotInvolvedException {
        if (trader == buyer) {
            return new PricedItemPosition(item, amount, totalValue);
        } else if (trader == seller) {
            return new PricedItemPosition(item, -amount, totalValue.invert());
        } else {
            throw new TraderNotInvolvedException();
        }
    }
}
