package ch.chrummibei.silvercoin.gui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by brachiel on 08/02/2017.
 */
public class Font extends Bitmap {
    public static final String FONT_PATH = "/resources/fonts/fixed_01.png";
    public static final Font FIXED_FONT;
    public static final String FONT_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!?_-.,;:$öäüÖÄÜ+='\"^*#~";
    public static final int CHAR_HEIGHT = 9;
    public static final int CHAR_WIDTH = 6;

    static {
        FIXED_FONT = new Font(loadStaticFontBuffer());
    }

    private static BufferedImage loadStaticFontBuffer() {
        InputStream inputStream = Font.class.getResourceAsStream(FONT_PATH);
        if (inputStream == null) {
            System.out.println("Font Image " + FONT_PATH + " cannot be found.");
            return null;
        }

        try {
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Font(BufferedImage fontImage) {
        super(fontImage.getWidth(), fontImage.getHeight());
        setRect(fontImage.getRaster());
    }
}
