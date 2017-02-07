package ch.chrummibei.silvercoin.universe.trade;

import ch.chrummibei.silvercoin.universe.credit.Credit;
import ch.chrummibei.silvercoin.universe.credit.TotalValue;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.position.PricedItemPosition;
import ch.chrummibei.silvercoin.universe.position.YieldingItemPosition;

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
    Map<Item,YieldingItemPosition> inventory = new HashMap<>();
    private ArrayList<Market> offersPresentAtMarket = new ArrayList<>();

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

    public Credit getCredits() {
        return credit;
    }
    public void addCredits(Credit credits) {
        credit.add(credits);
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

    public void offerTradesAt(Market market) {
        market.addAllOffers(offeredTrades);
        offersPresentAtMarket.add(market);
    }

    public void addToInventory(PricedItemPosition inventoryItem) {
        if (inventory.containsKey(inventoryItem.getItem())) {
            inventory.get(inventoryItem.getItem()).add(inventoryItem);
        } else {
            inventory.put(inventoryItem.getItem(), new YieldingItemPosition(inventoryItem));
        }
    }


    public void executeTrade(Trade trade) throws TraderNotInvolvedException {
        System.out.println(this + " is executing trade " + trade);

        PricedItemPosition newItemPosition = trade.getTradersItemPosition(this);
        // newItemPosition might be negative

        credit.isubtract(newItemPosition.getPurchaseValue());
        addToInventory(newItemPosition);
    }

    public void offerAccepted(TradeOffer offer) {
        if (offer.getAmount() <= 0) {
            offeredTrades.remove(offer);
            offersPresentAtMarket.forEach(m -> m.removeTradeOffer(offer));
        }
    }

    public double calcTotalProfit() {
        return inventory.values().stream()
                .map(YieldingItemPosition::getRealisedProfit)
                .mapToDouble(TotalValue::toDouble)
                .sum();
    }
}
