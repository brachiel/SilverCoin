package ch.chrummibei.silvercoin;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * A TradeOffer is a potential Trade offered by a Trader.
 */
public class TradeOffer {
    public enum TYPE {
        BUYING("Buy", "Buying", "is buying"), SELLING("Sell", "Selling", "is selling");

        TYPE(final String shortString,
             final String longString,
             final String verb) {
            this.shortString = shortString;
            this.longString = longString;
            this.verb = verb;
        }
        public String toString() { return this.longString; }

        public final String shortString; /* The string representation of this type */
        public final String longString; /* The string representation of this type */
        public final String verb; /* The string representation of this type */
    }

    private Trader trader;
    private Item item;
    private TYPE type;
    private int amount;
    private Price price;

    public TradeOffer(Trader trader, Item item, TYPE type, int amount, Price price) {
        this.trader = trader;
        this.item = item;
        this.type = type;
        this.amount = amount;
        this.price = price;
    }

    public boolean isSelling() {
        return type == TYPE.SELLING;
    }

    public boolean isBuying() {
        return type == TYPE.BUYING;
    }

    public Trader getTrader() {
        return trader;
    }

    public int getAmount() {
        return amount;
    }

    public Item getItem() {
        return item;
    }

    public TYPE getType() {
        return type;
    }

    public Price getPrice() {
        return price;
    }

    public String toString() {
        return trader + " " + type.verb + " " + item.getName() + " for " + price;
    }

    public String compactString() {
        return price + " (" + trader + ")";
    }
}
