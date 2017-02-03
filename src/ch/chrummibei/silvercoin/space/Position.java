package ch.chrummibei.silvercoin.space;

/**
 * The position of an object in space.
 */
public class Position extends Vector2d {
    public Position(double x, double y) {
        super(x, y);
    }

    public Position(Double[] coordinates) throws IllegalArgumentException {
        super(coordinates);
    }

    public Position(Vector2d vector) {
        super(vector);
    }

    @Override
    public Position add(Vector2d other) {
        return (Position) super.add(other);
    }
    @Override
    public Position subtract(Vector2d other) {
        return (Position) super.subtract(other);
    }
    @Override
    public Position multiply(double with) {
        return (Position) super.multiply(with);
    }
    @Override
    public Position divide(double with) {
        return (Position) super.divide(with);
    }
    @Override
    public Position reverse() {
        return (Position) super.reverse();
    }
}
