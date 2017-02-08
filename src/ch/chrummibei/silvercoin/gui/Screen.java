package ch.chrummibei.silvercoin.gui;

import ch.chrummibei.silvercoin.universe.space.Universe;
import ch.chrummibei.silvercoin.universe.trade.Market;

import java.awt.image.DataBufferInt;

/**
 * Created by brachiel on 07/02/2017.
 */
public class Screen extends Bitmap {
    public Screen(int width, int height) {
        super(width, height);
    }

    /*
    public void testScreen() {
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixels[x + width*y] = ((0xff*x)/width) << 16 + ((0xff*y)/height) << 8 + (0xff*(x+y))/(width+height);
            }
        }
        //writeString("Hello World!", 50, 50);
        draw(font, 0, 0, 100, 100);
    }
    */

    public void render(Universe universe) {
        Market market = universe.getMarkets().findFirst().get();

        Object[] items = market.searchTradedItems().toArray();
        final int itemLineHeight = Font.CHAR_HEIGHT + 1;
        for (int i = 0; i < items.length; ++i) {
            writeString(items[i].toString(), 5, 5 + i * itemLineHeight);
            System.out.println("Printing " + items[i].toString());
        }

        ((DataBufferInt) getDataBuffer()).getData()[ 50] = 0x00f;
        ((DataBufferInt) getDataBuffer()).getData()[100] = 0x0f0;
        ((DataBufferInt) getDataBuffer()).getData()[150] = 0xf00;
    }
}
