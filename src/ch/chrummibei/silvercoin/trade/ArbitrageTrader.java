package ch.chrummibei.silvercoin.trade;

import ch.chrummibei.silvercoin.actor.ArbitrageTradeActor;

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
            arbitrage.getBuy().accept(this);
            arbitrage.getSell().accept(this);
        } catch (TradeOfferAlreadyAcceptedException e) {
            // Tough titty.
            e.printStackTrace();
        }
    }
}
