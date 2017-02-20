package ch.chrummibei.silvercoin.config;

import ch.chrummibei.silvercoin.universe.Universe;

/**
 * Created by brachiel on 20/02/2017.
 */
public class RandomRangeConfig {
    private Double min;
    private Double max;

    public void setMin(Double min) {
        this.min = min;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public double getRandomDouble() {
        return Universe.getRandomDouble(min, max);
    }
    public int getRandomInt() {
        return Universe.getRandomInt((int) Math.floor(min), (int) Math.floor(max));
    }
}
