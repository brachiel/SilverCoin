package ch.chrummibei.silvercoin.gui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by brachiel on 08/02/2017.
 */
public class Font extends Bitmap {
    public static final String FONT_PATH = "resources/fonts/fixed_01.png";

    public Font(int width, int height) {
        super(width, height);

        InputStream inputStream = Font.class.getResourceAsStream(FONT_PATH);
        BufferedImage fontImage;
        try {
            fontImage = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        fontImage.getData().getDataBuffer()
    }
}
