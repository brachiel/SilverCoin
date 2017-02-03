package ch.chrummibei.silvercoin.trade;

import ch.chrummibei.silvercoin.credit.TotalValue;
import ch.chrummibei.silvercoin.item.Item;
import ch.chrummibei.silvercoin.position.PricedItemPosition;
import ch.chrummibei.silvercoin.credit.Credit;
import ch.chrummibei.silvercoin.position.YieldingItemPosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit capable of trading
 */
public class Trader extends Market {
    private static int traderNameSequence = 0;

    private String name;
    private Credit credit = new Credit(0.0);
    private ArrayList<TradeOffer> offeredTrades = new ArrayList<>();
    private Map<Item,YieldingItemPosition> inventory = new HashMap<>();

    public Trader() {
        this.name = "Trader " + String.valueOf(Trader.getNextTraderNameSequence());

        traderNameSequence += 1;
    }

    private static int getNextTraderNameSequence() {
        return(traderNameSequence++);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Credit getCredit() {
        return credit;
    }

    public void setCredit(Credit credit) {
        this.credit.set(credit);
    }
    public void setCredit(Double credit) {
        this.credit.set(credit);
    }

    public String toString() {
        return name;
    }

    public void addToInventory(PricedItemPosition inventoryItem) {
        if (inventory.containsKey(inventoryItem.getItem())) {
            inventory.get(inventoryItem.getItem()).add(inventoryItem);
        } else {
            inventory.put(inventoryItem.getItem(), new YieldingItemPosition(inventoryItem));
        }
    }


    public void executeTrade(Trade trade) throws TraderNotInvolvedException {
        PricedItemPosition newItemPosition = trade.getTradersItemPosition(this);
        // newItemPosition might be negative

        credit.isubtract(newItemPosition.getPurchaseValue());
        addToInventory(newItemPosition);
    }

    public double calcTotalProfit() {
        return inventory.values().stream()
                .map(YieldingItemPosition::getRealisedProfit)
                .mapToDouble(TotalValue::toDouble)
                .sum();
    }
}
