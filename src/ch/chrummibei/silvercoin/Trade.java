package ch.chrummibei.silvercoin;

/**
 * Trade containing amount and price of item to be traded.
 */
public class Trade {
    Item item;
    int amount; // Negative is selling
    Double price; // Positive is selling
    Trader buyer = null;
    Trader seller = null;

    public Trade(Trader seller, Trader buyer, Item item, int amount, Double price) {
        this.seller = seller;
        this.buyer = buyer;

        this.item = item;
        this.amount = amount;
        this.price = price;
    }


    public Item getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public Double getPrice() {
        return price;
    }

    public Trader getBuyer() {
        return buyer;
    }

    public Trader getSeller() {
        return seller;
    }
}
