package ch.chrummibei.silvercoin.gui;

import ch.chrummibei.silvercoin.config.Resources;
import sun.awt.image.IntegerInterleavedRaster;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.IOException;

/**
 * Default Font Bitmap containing a predefined set of characters.
 */
public class Font extends Bitmap {
    public static final Font FIXED_FONT;
    public static final String FONT_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!?_-.,;:$öäüÖÄÜ+='\"^*#~";
    public static final int CHAR_HEIGHT = 9;
    public static final int CHAR_WIDTH = 6;

    static {
        FIXED_FONT = new Font(loadStaticFontBuffer());
    }

    private static BufferedImage loadStaticFontBuffer() {
        try {
            return ImageIO.read(Resources.getDefaultFontStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Font(BufferedImage fontImage) {
        super(new IntegerInterleavedRaster(
                new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, fontImage.getWidth(), fontImage.getHeight(), new int[]{0xff0000, 0x00ff00, 0x0000ff, 0x1000000}),
                new Point(0,0)));
        raster.setRect(fontImage.getRaster());
        //colorToBitMask((byte) 0x000000); // Make black to mask
    }
}
