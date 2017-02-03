package ch.chrummibei.silvercoin.actor;

import ch.chrummibei.silvercoin.trade.Arbitrage;
import ch.chrummibei.silvercoin.trade.ArbitrageTrader;
import ch.chrummibei.silvercoin.trade.Market;

import java.util.Optional;

/**
 * Created by brachiel on 03/02/2017.
 */
public class ArbitrageTradeActor extends ArbitrageTrader implements Actor {
    Market currentMarket;

    public ArbitrageTradeActor(Market market) {
        currentMarket = market;
    }

    @Override
    public void tick(double timeDiff) {
        // TODO: Randomise the chosen Arbitrage
        Optional<Arbitrage> arbitrage = findArbitrages(currentMarket).findFirst();

        if (arbitrage.isPresent()) {
            System.out.println("ArbitrageTrader found an opportunity");
            executeArbitrage(arbitrage.get());
        } else {
            System.out.println("ArbitrageTrader got no luck");
        }
    }
}
