package ch.chrummibei.silvercoin.universe.actor;

import ch.chrummibei.silvercoin.universe.space.Acceleration;

/**
 * Created by brachiel on 03/02/2017.
 */
public interface AcceleratedActor extends MovingActor {
    Acceleration getAcceleration();
    void setAcceleration(Acceleration acceleration);

    default void tick(long timeDiffMillis) {
        setVelocity(getVelocity().add(getAcceleration().multiply(Double.valueOf(timeDiffMillis)/1000.0)));
        MovingActor.super.tick(timeDiffMillis);
    }
}
