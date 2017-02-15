package ch.chrummibei.silvercoin.universe.actor;

import ch.chrummibei.silvercoin.universe.space.Position;
import ch.chrummibei.silvercoin.universe.space.Velocity;

/**
 * Created by brachiel on 03/02/2017.
 */
public interface MovingActor extends PositionedActor {
    Position getPosition();
    void setPosition(Position position);

    Velocity getVelocity();
    void setVelocity(Velocity velocity);

    // Newton Step
    default void tick(long timeDiffMillis) {
        setPosition(getPosition().add(getVelocity()).multiply(Double.valueOf(timeDiffMillis)/1000.0));
    }
}
