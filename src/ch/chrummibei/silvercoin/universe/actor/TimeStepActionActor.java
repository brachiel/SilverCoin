package ch.chrummibei.silvercoin.universe.actor;

import java.util.Map;
import java.util.function.Consumer;

/**
 * TimeStepActionActor is an actor that executes specified actions every set amount of milliseconds.
 * This implementation only guarantees, that every action will be executed enough times; not that it will
 * be executed after exactly the right amount of milliseconds.
 */
public interface TimeStepActionActor extends Actor {
    class Timekeeper {
        public Long localTime = 0L;
        public Long periodicityInMillis;

        public Timekeeper(Long periodicityInMillis) {
            this.periodicityInMillis = periodicityInMillis;
        }
    }


    /**
     * Add an action to be executed every intervalMillis milliseconds.
     *
     * @param action      A lambda to be executed every intervalMillis milliseconds
     * @param periodicity The periodicityInMillis at which to execute the action.
     */
    void addAction(Consumer<Long> action, long periodicity);
    Map<Consumer<Long>,Timekeeper> getTimedActions();

    default void tick(long timeDiffMillis) {
        executeTimedActions(timeDiffMillis);
    }

    default void executeTimedActions(long timeDiffMillis) {
        getTimedActions().entrySet().forEach(entry -> {
            long periodicity = entry.getValue().periodicityInMillis;
            entry.getValue().localTime += timeDiffMillis;
            while (periodicity < entry.getValue().localTime) {
                entry.getKey().accept(periodicity);
                entry.getValue().localTime -= periodicity;
            }
        });
    }


}
