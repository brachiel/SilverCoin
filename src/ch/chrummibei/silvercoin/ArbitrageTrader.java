package ch.chrummibei.silvercoin;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * An ArbitrageTrader is a Trader who is able to find Arbitrages
 */
public class ArbitrageTrader extends Trader {
    public static Stream<Arbitrage> findArbitrages(Market market) {
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
}
