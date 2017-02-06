package ch.chrummibei.silvercoin.universe.actor;

import ch.chrummibei.silvercoin.universe.space.Position;
import ch.chrummibei.silvercoin.universe.space.Velocity;

/**
 * Created by brachiel on 03/02/2017.
 */
public abstract class MovingActor extends PositionedActor {
    Velocity velocity;

    public MovingActor(Position position, Velocity velocity) {
        super(position);
        this.velocity = velocity;
    }

    public MovingActor(Position position) {
        this(position, new Velocity(0,0));
    }

    @Override
    // Newton Step
    public void tick(double timeDiff) {
        position.iadd(velocity.multiply(timeDiff));
    }
}
