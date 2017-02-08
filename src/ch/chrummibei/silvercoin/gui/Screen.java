package ch.chrummibei.silvercoin.gui;

import ch.chrummibei.silvercoin.universe.space.Universe;
import ch.chrummibei.silvercoin.universe.trade.Market;

/**
 * Created by brachiel on 07/02/2017.
 */
public class Screen extends Bitmap {
    public Screen(int width, int height) {
        super(width, height);
    }

    public void testScreen() {
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                pixels[x + width*y] = ((0xff*x)/width) << 16 + ((0xff*y)/height) << 8 + (0xff*(x+y))/(width+height);
            }
        }
        //writeString("Hello World!", 50, 50);
        draw(font, 0, 0, 100, 100);
    }

    public void render(Universe universe) {
        Market market = universe.getMarkets().findFirst().get();

        Object[] items = market.searchTradedItems().toArray();
        final int itemLineHeight = Fonts.FONT_SIZE + 1;
        for (int i = 0; i < items.length; ++i) {
            writeString(items[i].toString(), 5, 5 + i * itemLineHeight);
        }
    }
}
