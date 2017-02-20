package ch.chrummibei.silvercoin.universe.trade;

import ch.chrummibei.silvercoin.universe.credit.Credit;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.credit.TotalValue;
import ch.chrummibei.silvercoin.universe.entity_systems.MarketUtil;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.position.YieldingItemPosition;

import java.util.Map;
import java.util.Optional;

/**
 * Unit capable of trading
 */
public class Trader extends MarketUtil {
    private static int traderNameSequence = 0;



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

    public void offerTradesAt(MarketUtil market) {
        market.addAllOffers(getOfferedTrades());
        offersPresentAtMarkets.add(market);
    }

    @Override
    public void addTradeOffer(TradeOffer offer) {
        //System.out.println(this.toString() + " is adding this trade: " + offer);
        TradeOffer restOffer = findAndAcceptOppositeTradeOffersInMarkets(offer);
        if (restOffer == null) return; // We could find all trades in markets already. No need to offer this trade anymore.

        super.addTradeOffer(restOffer);
        offersPresentAtMarkets.forEach(market -> market.addTradeOffer(restOffer));
    }

    @Override
    public void removeTradeOffer(TradeOffer offer) {
        super.removeTradeOffer(offer);
        offersPresentAtMarkets.forEach(market -> market.removeTradeOffer(offer));
    }

    /**
     * Takes a trade offer and executes all opposing trades with equal or cheaper price until there is either no more
     * amount to trade, or there are no more applicable trade offers found.
     * @param offer The trade offer we're trying to find opposing trades for
     * @return A trade offer which has the amount that is left to trade, or null if all were traded.
     */
    public TradeOffer findAndAcceptOppositeTradeOffersInMarkets(TradeOffer offer) {
        int leftToTrade = offer.getAmount();
        if (leftToTrade == 0) throw new RuntimeException("Finding trades with amount=0. This is bug.");

        // TODO: If multiple markets exist, this will not choose the best trades.
        for (MarketUtil market : offersPresentAtMarkets) {
            for (Map.Entry<TradeOffer, Integer> entry : market.getTradeOffersToTradeAmount(offer.getItem(), offer.getType().opposite(), offer.getAmount()).entrySet()) {
                TradeOffer opposingOffer = entry.getKey();

                // When offers are selling, the singed Price is positive
                // SO: We sell, they buy, we must have: our price <= -their price for us to make a profit in arbitrage
                if (opposingOffer.getSignedPriceDouble() > -offer.getSignedPriceDouble()) {
                    // This is a a bad trade for us. Since it's ordered, we don't need to search further.
                    break;
                }

                try {
                    //System.out.println("Found opposing trade offer: " + opposingOffer);
                    opposingOffer.accept(offer.getOfferingTrader(), entry.getValue()); // Trade :)
                    leftToTrade -= entry.getValue();

                    if (leftToTrade <= 0) { // We're done.
                        return null;
                    }
                } catch (TradeOfferHasNotEnoughAmountLeft e) { // Someone else already accepted this trade :/. Skip it.
                    e.printStackTrace();
                }
            }
        }

        if (leftToTrade > 0) {
            return new TradeOffer(offer.getOfferingTrader(), offer.getItem(), offer.getType(), leftToTrade, offer.getPrice());
        } else {
            return null;
        }
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
        if (amount == 0) throw new RuntimeException("Setting unique trade offer with amount=0. This is a bug.");

        Optional<TradeOffer> existingTradeOffer = searchOfferedTrades(item, type).findAny();

        if (existingTradeOffer.isPresent()) {
            existingTradeOffer.get().updateAmount(amount, price);
        } else {
            TradeOffer newTradeOffer = new TradeOffer(this, item, type, amount, price);
            addTradeOffer(newTradeOffer);
        }
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
