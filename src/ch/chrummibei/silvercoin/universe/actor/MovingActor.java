package ch.chrummibei.silvercoin.universe.actor;

import ch.chrummibei.silvercoin.universe.space.Position;
import ch.chrummibei.silvercoin.universe.space.Velocity;

/**
 * Created by brachiel on 03/02/2017.
 */
public abstract class MovingActor implements PositionedActor {
    Position position;
    Velocity velocity;

    public MovingActor(Position position, Velocity velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    public MovingActor(Position position) {
        this(position, new Velocity(0,0));
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    // Newton Step
    public void tick(long timeDiffMillis) {
        position.iadd(velocity.multiply(Double.valueOf(timeDiffMillis)/1000.0));
    }
}
