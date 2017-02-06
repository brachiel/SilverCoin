package ch.chrummibei.silvercoin.universe.actor;

import ch.chrummibei.silvercoin.universe.trade.Arbitrage;
import ch.chrummibei.silvercoin.universe.trade.ArbitrageTrader;
import ch.chrummibei.silvercoin.universe.trade.Market;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Created by brachiel on 03/02/2017.
 */
public class ArbitrageTradeActor extends ArbitrageTrader implements TimestepActionActor {
    Map<Consumer<Long>,Timekeeper> actions = new HashMap<>();

    public ArbitrageTradeActor(Market market) {
        super(market);
        addAction(this::findAndExecuteArbitrage, 1000);
    }

    @Override
    public void addAction(Consumer<Long> action, long periodicity) {
        actions.put(action, new Timekeeper(periodicity));
    }

    @Override
    public Map<Consumer<Long>, Timekeeper> getTimedActions() {
        return actions;
    }

    public void findAndExecuteArbitrage(long timeDiffMillis) {
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
