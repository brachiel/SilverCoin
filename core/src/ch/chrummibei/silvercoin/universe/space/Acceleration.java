package ch.chrummibei.silvercoin.universe.space;

/**
 * The Acceleration of an object in space.
 */
public class Acceleration extends Vector2d {
    public Acceleration(double x, double y) {
        super(x, y);
    }

    public Acceleration(Double[] coordinates) throws IllegalArgumentException {
        super(coordinates);
    }

    public Acceleration(Vector2d vector) {
        super(vector);
    }

    @Override
    public Acceleration add(Vector2d other) {
        return (Acceleration) super.add(other);
    }
    @Override
    public Acceleration subtract(Vector2d other) {
        return (Acceleration) super.subtract(other);
    }
    @Override
    public Acceleration multiply(double with) {
        return (Acceleration) super.multiply(with);
    }
    @Override
    public Acceleration divide(double with) {
        return (Acceleration) super.divide(with);
    }
    @Override
    public Acceleration reverse() {
        return (Acceleration) super.reverse();
    }
}
