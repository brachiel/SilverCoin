package ch.chrummibei.silvercoin.universe.actor;

import ch.chrummibei.silvercoin.universe.space.Position;
import ch.chrummibei.silvercoin.universe.space.Velocity;

/**
 * A positioned actor that can move around. Time step is done by newton method (for now).
 */
public interface MovingActor extends PositionedActor {
    Position getPosition();
    void setPosition(Position position);

    Velocity getVelocity();
    void setVelocity(Velocity velocity);

    // Newton Step
    default void tick(long timeDiffMillis) {
        setPosition(getPosition().add(getVelocity()).multiply((double) timeDiffMillis /1000.0));
    }
}
