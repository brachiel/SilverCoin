package ch.chrummibei.silvercoin.universe.actor;

import ch.chrummibei.silvercoin.universe.space.Acceleration;
import ch.chrummibei.silvercoin.universe.space.Position;
import ch.chrummibei.silvercoin.universe.space.Velocity;

/**
 * Created by brachiel on 03/02/2017.
 */
public abstract class AcceleratedActor extends MovingActor {
    Acceleration acceleration;

    public AcceleratedActor(Position position, Velocity velocity, Acceleration acceleration) {
        super(position, velocity);
        this.acceleration = acceleration;
    }

    public AcceleratedActor(Position position, Velocity velocity) {
        this(position, velocity, new Acceleration(0,0));
    }

    public AcceleratedActor(Position position) {
        this(position, new Velocity(0,0), new Acceleration(0,0));
    }

    @Override
    // Newton Step
    public void tick(long timeDiffMillis) {
        velocity.iadd(acceleration.multiply(Double.valueOf(timeDiffMillis)/1000.0));
        super.tick(timeDiffMillis);
    }
}
