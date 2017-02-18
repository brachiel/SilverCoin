package ch.chrummibei.silvercoin.universe.trade;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * An ArbitrageTrader is a Trader who is able to find Arbitrages
 */
public class ArbitrageTrader extends Trader {
    Market market;

    public ArbitrageTrader(Market market) {
        this.market = market;
    }

    public Stream<Arbitrage> findArbitrages() {
        return market.searchTradedItems()
                // Make Arbitrage where possible, empty else
                .map(item -> {
                    // From an arbitrage kind of view, we buy when a trader sells
                    Optional<TradeOffer> bestSell = market.searchBestBuyingTrade(item);
                    Optional<TradeOffer> bestBuy = market.searchBestSellingTrade(item);
                    Optional<Arbitrage> arbitrage;

                    if (bestBuy.isPresent() && bestSell.isPresent()
                            && bestBuy.get().getPrice().toDouble() < bestSell.get().getPrice().toDouble()) {
                        arbitrage = Optional.of(new Arbitrage(bestBuy.get(), bestSell.get()));
                    } else {
                        arbitrage = Optional.empty();
                    }

                    return arbitrage;
                })
                .filter(Optional::isPresent) // Filter out the empty arbitrages
                .map(Optional::get); // Convert Optional<Arbitrage> to Arbitrage
    }

    public void executeArbitrage(Arbitrage arbitrage) {
        try {
            arbitrage.getBuy().accept(this, arbitrage.getTradableAmount());
            arbitrage.getSell().accept(this, arbitrage.getTradableAmount());
        } catch (TradeOfferHasNotEnoughAmountLeft e) {
            // Tough titty.
            e.printStackTrace();
        }
    }

    public void printShortStatus() {
        System.out.println(this + " traded for a profit of " + calcTotalProfit());
    }

    public void printLongStatus() {
        System.out.println(this + " traded in the following items:");
        inventory.values().forEach(pos -> System.out.println(pos.getItem() + " for a profit of: " + pos.getRealisedProfit()));
    }

    public Market getMarket() {
        return market;
    }
}
