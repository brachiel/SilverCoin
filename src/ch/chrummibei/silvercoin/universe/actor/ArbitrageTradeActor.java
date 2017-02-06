package ch.chrummibei.silvercoin.universe.actor;

import ch.chrummibei.silvercoin.universe.trade.Arbitrage;
import ch.chrummibei.silvercoin.universe.trade.ArbitrageTrader;
import ch.chrummibei.silvercoin.universe.trade.Market;

import java.util.Random;

/**
 * Created by brachiel on 03/02/2017.
 */
public class ArbitrageTradeActor extends ArbitrageTrader implements Actor {
    public ArbitrageTradeActor(Market market) {
        super(market);
    }

    @Override
    public void tick(double timeDiff) {
        // TODO: Use a stream for randomisation

        // The following implements http://stackoverflow.com/questions/23351918/select-an-element-from-a-stream-with-uniform-distributed-probability
        Random random = new Random();
        Object[] arbitrages = findArbitrages().toArray();
        Arbitrage arbitrage = null;
        for (int i = 0; i < arbitrages.length; ++i) {
            if (random.nextDouble() < 1.0/(i+1)) {
                arbitrage = (Arbitrage) arbitrages[i];
            }
        }

        if (arbitrage != null) {
            System.out.println("ArbitrageTrader found an opportunity: " + arbitrage);
            executeArbitrage(arbitrage);
        } else {
            System.out.println("ArbitrageTrader got no luck");
        }

        printShortStatus();
    }
}
