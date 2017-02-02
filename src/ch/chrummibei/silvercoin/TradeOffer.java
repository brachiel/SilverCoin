package ch.chrummibei.silvercoin;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Created by brachiel on 02/02/2017.
 */
public class TradeOffer {
    public enum TYPE {
        BUYING, SELLING
    }

    public static final EnumMap<TYPE,String> offerTypeName;
    static {
        EnumMap<TYPE,String> tmpMap = new EnumMap<TYPE,String>(TYPE.class);
        tmpMap.put(TYPE.SELLING,"Selling");
        tmpMap.put(TYPE.BUYING,"Buying");
        offerTypeName = Collections.unmodifiable(tmpMap);
    }

    Trader trader;
    Item item;
    int direction;
    int amount;
    double price;

    public TradeOffer(Trader trader, Item item, int direction, int amount, double price) {
        this.trader = trader;
        this.item = item;
        if (direction == type.SELLING || direction == type.BUYING) {
            this.direction = direction;
        } else {
            throw new IllegalArgumentException("Direction has to be valid");
        }
        this.amount = amount;
        this.price = price;
    }

    public boolean isSelling() {
        return direction == SELLING;
    }

    public boolean isBuying() {
        return direction == BUYING;
    }

    public Trader getTrader() {
        return trader;
    }

    public Item getItem() {
        return item;
    }

    public int getDirection() {
        return direction;
    }

    public double getPrice() {
        return price;
    }

    public String toString() {
        return item.getName() + " for " + String.format("%.2f", price);
    }
}
