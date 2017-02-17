package ch.chrummibei.silvercoin.gui;

import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;

/**
 * Raw pixel based surface to paint on. Only works in INT ARGB format with BITMASK transparency.
 */
public class Bitmap {
    protected final WritableRaster raster;
    protected final int[] pixels;
    private final int width;
    private final int height;

    public Bitmap(WritableRaster parentRaster) {
        raster = parentRaster;
        width = parentRaster.getWidth();
        height = parentRaster.getHeight();
        pixels = ((DataBufferInt) raster.getDataBuffer()).getData();
    }

    /**
     * Draw translated srcBitmap onto this one at location dx, dy
     * Copies every (x+tx, y+ty) of the srcBitmap onto (x+dy, y+dy) on this Bitmap
     * @param srcBitmap Other srcBitmap
     * @param tx X translation of srcBitmap
     * @param ty Y translation of srcBitmap
     * @param dx target X
     * @param dy target Y
     */
    void draw(Bitmap srcBitmap, int tx, int ty, int dx, int dy) {
        raster.setRect(dx, dy, srcBitmap.raster.createTranslatedChild(tx, ty));
    }

    /**
     * Draw translated srcBitmap onto this one at location dx, dy
     * Copies every (x, y) of the srcBitmap onto (x+dy, y+dy) on this Bitmap
     * @param srcBitmap Other srcBitmap
     * @param dx target X
     * @param dy target Y
     */
    void draw(Bitmap srcBitmap, int dx, int dy) {
        raster.setRect(dx, dy, srcBitmap.raster);
    }


    void drawRect(Bitmap srcBitmap, int tx, int ty, int width, int height, int dx, int dy) {
        raster.setRect(dx, dy, srcBitmap.raster.createChild(tx, ty, width, height, 0, 0, null));
    }

    void clearToTransparent() {
        for (int i = 0; i < pixels.length; ++i) {
            pixels[i] = 0x0000000; // Black transparent
        }
    }

    void drawTransparentRect(Bitmap srcBitmap, int tx, int ty, int w, int h, int dx, int dy, int aColor) {
        int minX = Math.max(0, Math.max(tx, dx));
        int minY = Math.max(0, Math.max(ty, dy));
        int maxX = Math.min(w, Math.max(width+dx, srcBitmap.width+tx));
        int maxY = Math.min(h, Math.max(height+dy, srcBitmap.height+ty));

        for (int x = minX; x < maxX; ++x) {
            for (int y = minY; y < maxY; ++y) {
                int srcPixel = srcBitmap.pixels[(x+tx) + (y+ty)*srcBitmap.width];
                if (srcPixel != aColor) {
                    pixels[(x + dx) + (y + dy) * width] = srcPixel | 0x1000000;
                }
            }
        }
    }

    void drawTransparentRect(Bitmap srcBitmap, int tx, int ty, int w, int h, int dx, int dy) {
        int minX = Math.max(0, Math.max(-tx, -dx)); // dx < x -> continue
        int maxX = Math.min(w, Math.min(width-Math.max(0,dx), srcBitmap.width-Math.max(0,tx)));

        int minY = Math.max(0, Math.max(-ty, -dy));
        int maxY = Math.min(h, Math.min(height-Math.max(0,dy), srcBitmap.height-Math.max(0,ty)));

        for (int x = minX; x < maxX; ++x) {
            for (int y = minY; y < maxY; ++y) {
                if ((x + dx) + (y + dy) * width > pixels.length) {
                    System.out.println("NO!");
                }
                int srcPixel = srcBitmap.pixels[(x+tx) + (y+ty)*srcBitmap.width];
                if ((srcPixel & 0x1000000) != 0) { // Not transparent
                    pixels[(x + dx) + (y + dy) * width] = srcPixel | 0x1000000; // Make opaque
                }
            }
        }
    }

    void colorToBitMask(byte color) {
        for (int i = 0; i < pixels.length; ++i) {
            if ((pixels[i] & 0x0ffffff) == color) { // is color?
                pixels[i] = pixels[i] & 0x0ffffff; // Make transparent
            }
        }

    }

    void writeString(String string, int dx, int dy) {
        for (int i = 0; i < string.length(); ++i) {
            int sourceXOffset = Font.CHAR_WIDTH * Font.FONT_STRING.indexOf(string.charAt(i));
            if (! (sourceXOffset >= 0)) continue;
            drawTransparentRect(Font.FIXED_FONT, sourceXOffset, 0, Font.CHAR_WIDTH, Font.CHAR_HEIGHT, dx + Font.CHAR_WIDTH * i, dy);
        }
    }
}
