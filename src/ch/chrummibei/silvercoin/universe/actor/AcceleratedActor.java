package ch.chrummibei.silvercoin.universe.actor;

import ch.chrummibei.silvercoin.universe.space.Acceleration;

/**
 * Moving actor that can have an acceleration. Time step is by newton method (for now).
 */
public interface AcceleratedActor extends MovingActor {
    Acceleration getAcceleration();
    void setAcceleration(Acceleration acceleration);

    default void tick(long timeDiffMillis) {
        setVelocity(getVelocity().add(getAcceleration().multiply((double) timeDiffMillis /1000.0)));
        MovingActor.super.tick(timeDiffMillis);
    }
}
