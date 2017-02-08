package ch.chrummibei.silvercoin.gui;

import java.util.stream.IntStream;

import static java.lang.Math.min;

/**
 * Created by brachiel on 06/02/2017.
 */
public class Bitmap {
    public static final Bitmap font = Fonts.getFontBitmap();

    public final int width;
    public final int height;
    public final int[] pixels;

    public Bitmap(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new int[width*height];
    }

    void draw(Bitmap bitmap, int srcXOff, int srcYOff, int trgXOff, int trgYOff) {
        // Parallel draw
        IntStream.rangeClosed(0,min(width,bitmap.width)-1).parallel().forEach(x ->
            IntStream.rangeClosed(0,min(height,bitmap.height)-1).parallel().forEach(y -> {
                // TODO: This logic isn't needed if we're clever about the ranges.
                int tx = x + trgXOff;
                int ty = y + trgYOff;
                int sx = x + srcXOff;
                int sy = y + srcYOff;

                if (tx < 0 || width <= tx) return;
                if (ty < 0 || height <= ty) return;
                if (sx < 0 || bitmap.width <= sx) return;
                if (sy < 0 || bitmap.height <= sy) return;

                pixels[tx + width * ty] = bitmap.pixels[sx + bitmap.width * sy];
            })
        );
    }

    void writeString(String string, int targetXOffset, int targetYOffset) {
        for (int i = 0; i < string.length(); ++i) {
            int sourceXOffset = Fonts.CHAR_WIDTH * Fonts.FONT_STRING.indexOf(string.charAt(i));
            draw(font, sourceXOffset, 0, targetXOffset + Fonts.CHAR_WIDTH * i, targetYOffset);
        }
    }
}
