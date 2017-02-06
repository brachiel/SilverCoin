package ch.chrummibei.silvercoin.universe.space;

/**
 * Created by brachiel on 03/02/2017.
 */
public class Vector2d {
    private Double[] coordinates = new Double[2];

    public Vector2d(double x, double y) {
        coordinates[0] = x;
        coordinates[1] = y;
    }

    public Vector2d(Double[] coordinates) throws IllegalArgumentException {
        if (coordinates.length != 2) {
            throw new IllegalArgumentException("Double Array has to be of length 2");
        }
        this.coordinates[0] = coordinates[0];
        this.coordinates[1] = coordinates[1];
    }

    public Vector2d(Vector2d vector) {
        this.coordinates = vector.coordinates.clone();
    }

    public Vector2d add(Vector2d other) {
        return new Vector2d(this.coordinates[0] + other.coordinates[0], this.coordinates[1] + other.coordinates[1]);
    }
    public Vector2d subtract(Vector2d other) {
        return new Vector2d(this.coordinates[0] - other.coordinates[0], this.coordinates[1] - other.coordinates[1]);
    }
    public Vector2d multiply(double with) {
        return new Vector2d(with * this.coordinates[0], with * this.coordinates[1]);
    }
    public Vector2d divide(double with) {
        return multiply(1/with);
    }
    public Vector2d reverse() {
        return multiply(-1);
    }

    // in-place operations
    public void iadd(Vector2d other) {
        this.coordinates[0] += other.coordinates[0];
        this.coordinates[1] += other.coordinates[1];
    }
    public void isubtract(Vector2d other) {
        this.coordinates[0] -= other.coordinates[0];
        this.coordinates[1] -= other.coordinates[1];
    }
    public void imultiply(double with) {
        this.coordinates[0] *= with;
        this.coordinates[1] *= with;
    }
    public void idivide(double with) {
        imultiply(1/with);
    }
    public void ireverse() {
        imultiply(-1);
    }


    public double length() {
        return Math.sqrt(coordinates[0]*coordinates[0] + coordinates[1]*coordinates[1]);
    }

    public double distanceTo(Vector2d other) {
        return this.subtract(other).length();
    }

    @Override
    public Vector2d clone() {
        return new Vector2d(this);
    }

    @Override
    public String toString() {
        return String.format("(%.02f,%.02f)", coordinates[0], coordinates[1]);
    }
}
