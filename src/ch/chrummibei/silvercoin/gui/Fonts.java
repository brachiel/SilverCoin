package ch.chrummibei.silvercoin.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.IntStream;

/**
 * Created by brachiel on 07/02/2017.
 */
public class Fonts {
    /*
    public static final String FIXED = "../resources/fonts/fixed_01.ttf";
    public static final int FONT_SIZE = 8;
    public static final int CHAR_WIDTH = FONT_SIZE;
    public static final String FONT_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!?$.,_-";
    public static final int FONT_BITMAP_HEIGHT = FONT_SIZE;
    public static final int FONT_BITMAP_WIDTH = FONT_STRING.length() * CHAR_WIDTH;

    public static Font getFont() {
        InputStream inputStream = Fonts.class.getResourceAsStream(FIXED);
        try {
            return Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(Font.PLAIN, FONT_SIZE);
        } catch (FontFormatException e) {
            System.err.println(FIXED + " not loaded.  Using serif font.");
            return new Font(Font.MONOSPACED, Font.PLAIN, FONT_SIZE);
        } catch (IOException e) {
            System.err.println(FIXED + " not loaded.  Using serif font.");
            return new Font(Font.MONOSPACED, Font.PLAIN, FONT_SIZE);
        }
    }

    public static Bitmap getFontBitmap() {
        Font font = getFont();

        BufferedImage image = new BufferedImage(FONT_BITMAP_WIDTH, FONT_BITMAP_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, FONT_BITMAP_WIDTH, FONT_BITMAP_HEIGHT);
        graphics.setColor(Color.WHITE);
        graphics.setFont(font);
        graphics.drawString(FONT_STRING, 0, 0);

        Bitmap bitmap = new Bitmap(FONT_BITMAP_WIDTH, FONT_BITMAP_HEIGHT);
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        IntStream.rangeClosed(0, FONT_BITMAP_WIDTH*FONT_BITMAP_HEIGHT-1).parallel().forEach(
                i -> bitmap.pixels[i] = pixels[i]
        );

        return bitmap;
    }
    */
}
