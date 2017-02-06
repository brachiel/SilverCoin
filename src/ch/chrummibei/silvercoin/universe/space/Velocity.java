package ch.chrummibei.silvercoin.universe.space;

/**
 * The velocity of an object in space.
 */
public class Velocity extends Vector2d {
    public Velocity(double x, double y) {
        super(x, y);
    }

    public Velocity(Double[] coordinates) throws IllegalArgumentException {
        super(coordinates);
    }

    public Velocity(Vector2d vector) {
        super(vector);
    }

    @Override
    public Velocity add(Vector2d other) {
        return (Velocity) super.add(other);
    }
    @Override
    public Velocity subtract(Vector2d other) {
        return (Velocity) super.subtract(other);
    }
    @Override
    public Velocity multiply(double with) {
        return (Velocity) super.multiply(with);
    }
    @Override
    public Velocity divide(double with) {
        return (Velocity) super.divide(with);
    }
    @Override
    public Velocity reverse() {
        return (Velocity) super.reverse();
    }
}
