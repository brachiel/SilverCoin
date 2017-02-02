package ch.chrummibei.silvercoin;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * Unit capable of trading
 */
public class Trader extends Market {
    static int traderNameSequence = 0;

    String name;
    Double balance = 0.0;
    ArrayList<TradeOffer> offeredTrades = new ArrayList<>();
    ArrayList<InventoryItem> inventory = new ArrayList<>();

    public Trader() {
        this.name = "Trader " + String.valueOf(Trader.getNextTraderNameSequence());

        traderNameSequence += 1;
    }

    public static int getNextTraderNameSequence() {
        return(traderNameSequence++);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }


}
