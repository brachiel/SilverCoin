package ch.chrummibei.silvercoin.universe.actor;

/**
 * A thing that ticks. Will be added to the universe and ticks there.
 */
public interface Actor {
    void tick(long timeStepMillis);
}
