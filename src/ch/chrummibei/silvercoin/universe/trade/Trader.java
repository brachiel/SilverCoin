package ch.chrummibei.silvercoin.universe.trade;

import ch.chrummibei.silvercoin.universe.credit.Credit;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.credit.TotalValue;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.position.PricedItemPosition;
import ch.chrummibei.silvercoin.universe.position.YieldingItemPosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Unit capable of trading
 */
public class Trader extends Market {
    private static int traderNameSequence = 0;

    protected final Map<Item,YieldingItemPosition> inventory = new HashMap<>();

    private String name;
    private Credit credit = new Credit(0.0);
    private final ArrayList<Market> offersPresentAtMarkets = new ArrayList<>();

    public Trader(String name) {
        this.name = name;
    }

    public Trader() {
        this.name = "Trader " + String.valueOf(Trader.getNextTraderNameSequence());
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
        offersPresentAtMarkets.add(market);
    }

    @Override
    public void addTradeOffer(TradeOffer offer) {
        super.addTradeOffer(offer);
        offersPresentAtMarkets.forEach(market -> market.addTradeOffer(offer));
    }

    @Override
    public void removeTradeOffer(TradeOffer offer) {
        super.removeTradeOffer(offer);
        offersPresentAtMarkets.forEach(market -> market.removeTradeOffer(offer));
    }

    /**
     * Replaces the amount and price of an existing trade offer for that item and type,
     * or create a new trade offer.
     * @param item The item to be bought or sold.
     * @param type The type of offer BUY/SELL. Unique together with item
     * @param amount Amount to be bought/sold
     * @param price Price of one item
     */
    public void setUniqueTradeOffer(Item item, TradeOffer.TYPE type, int amount, Price price) {
        Optional<TradeOffer> existingTradeOffer = searchOfferedTrades(item, type).findAny();

        if (existingTradeOffer.isPresent()) {
            existingTradeOffer.get().updateAmount(amount, price);
        } else {
            TradeOffer newTradeOffer = new TradeOffer(this, item, type, amount, price);
            addTradeOffer(newTradeOffer);
        }
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

        credit.iSubtract(newItemPosition.getPurchaseValue());
        addToInventory(newItemPosition);
    }

    public void offerAccepted(TradeOffer offer) {
        if (offer.getAmount() <= 0) {
            removeTradeOffer(offer);
        }
    }

    public double calcTotalProfit() {
        return inventory.values().stream()
                .map(YieldingItemPosition::getRealisedProfit)
                .mapToDouble(TotalValue::toDouble)
                .sum();
    }
}
