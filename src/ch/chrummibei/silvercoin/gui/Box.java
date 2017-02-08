package ch.chrummibei.silvercoin.gui;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by brachiel on 08/02/2017.
 */
public class Box {
    final int x1, x2, y1, y2, width, height;

    public Box(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.width = x2 - x1;
        this.height = y2 - y1;
    }

    /**
     * Get the overlap of this box with the other boxes.
     * @param box Another box
     * @return The overlap of the two
     */
    public Box getOverlap(Box box) {
        return new Box(max(x1, box.x1), max(y1, box.y1),
                       min(x2, box.x2), min(y2, box.y2));
    }
}

