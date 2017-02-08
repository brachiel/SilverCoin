package ch.chrummibei.silvercoin.gui;

import java.awt.*;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.stream.IntStream;

import static java.lang.Math.min;

/**
 * Created by brachiel on 06/02/2017.
 */
public class Bitmap extends WritableRaster {
    public final int width;
    public final int height;

    public Bitmap(int width, int height) {
        super(DirectColorModel.getRGBdefault().createCompatibleSampleModel(width, height), new Point(0,0));

        this.width = width;
        this.height = height;
    }

    /**
     * Draw translated srcRaster onto this one at location dx, dy
     * Copies every (x+tx, y+ty) of the srcRaster onto (x+dy, y+dy) on this Bitmap
     * @param srcRaster Other srcRaster
     * @param tx X translation of srcRaster
     * @param ty Y translation of srcRaster
     * @param dx target X
     * @param dy target Y
     */
    void draw(Raster srcRaster, int tx, int ty, int dx, int dy) {
        setRect(dx, dy, srcRaster.createTranslatedChild(tx, ty));
    }

    /**
     * Draw translated srcRaster onto this one at location dx, dy
     * Copies every (x, y) of the srcRaster onto (x+dy, y+dy) on this Bitmap
     * @param srcRaster Other srcRaster
     * @param dx target X
     * @param dy target Y
     */
    void draw(Raster srcRaster, int dx, int dy) {
        setRect(dx, dy, srcRaster);
    }

    void drawRect(Raster srcRaster, int tx, int ty, int width, int height, int dx, int dy) {
        draw(srcRaster.createChild(0, 0, width, height, tx, ty, null), dx, dy);
    }

    void writeString(String string, int dx, int dy) {
        for (int i = 0; i < string.length(); ++i) {
            int sourceXOffset = Font.CHAR_WIDTH * Font.FONT_STRING.indexOf(string.charAt(i));
            drawRect(Font.FIXED_FONT, sourceXOffset, 0, Font.CHAR_WIDTH, Font.CHAR_HEIGHT, dx + Font.CHAR_WIDTH * i, dy);
        }
    }
}
