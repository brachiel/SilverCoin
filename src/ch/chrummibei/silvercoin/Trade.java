package ch.chrummibei.silvercoin;

/**
 * Trade containing amount and price of item to be traded.
 */
public class Trade {
    private Item item;
    private int amount;
    private Credit totalValue;
    private Trader buyer = null;
    private Trader seller = null;

    public Trade(Trader seller, Trader buyer, Item item, int amount, Price price) {
        this.seller = seller;
        this.buyer = buyer;

        this.item = item;
        this.amount = amount;
        this.totalValue = new Credit(amount * price.toDouble());
    }

    public Trade(Trader seller, Trader buyer, Item item, int amount, Credit totalValue) {
        this.seller = seller;
        this.buyer = buyer;

        this.item = item;
        this.amount = amount;
        this.totalValue = totalValue;
    }

    public Trade(TradeOffer offer) {

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
}
