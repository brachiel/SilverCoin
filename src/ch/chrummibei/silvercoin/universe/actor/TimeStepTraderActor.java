package ch.chrummibei.silvercoin.universe.actor;

import ch.chrummibei.silvercoin.universe.trade.Trader;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Connects Trader with TimeStepAction interface. Does nothing else.
 */
public abstract class TimeStepTraderActor extends Trader implements TimeStepActor {
    final Map<Consumer<Long>,Timekeeper> timedActions = new HashMap<>();

    // TimeStepActor methods
    @Override
    public void addAction(Consumer<Long> action, long periodicity) {
        timedActions.put(action, new Timekeeper(periodicity));
    }

    @Override
    public Map<Consumer<Long>, Timekeeper> getTimedActions() {
        return timedActions;
    }


}
