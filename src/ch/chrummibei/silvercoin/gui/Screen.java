package ch.chrummibei.silvercoin.gui;

import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.trade.Market;

import java.awt.image.DataBufferInt;

/**
 * Created by brachiel on 07/02/2017.
 */
public class Screen extends Bitmap {
    public Screen(int width, int height) {
        super(width, height);
    }
    public Screen(int width, int height, int colorMode) {
        super(width, height, colorMode);
    }

    public void testScreen() {
        int[] pixels = ((DataBufferInt) getDataBuffer()).getData();

        draw(Font.FIXED_FONT, 0, 0, 0, 0);
/*
        for (int x = 0; x < width; ++x) {
            for (int y = 200; y < height; ++y) {
                //pixels[x + width*y] = pixels[(x % 10) + width*y];
                pixels[x + width*y] = 0xff << 24 + ((0x55*x)/width) << 16 + ((0x55*y)/height) << 8 + (0x55*(x+y))/(width+height);
            }
        }
*/
        writeString("Hello World!", 50, 50);
    }


    public void render(Universe universe) {
        Market market = universe.getMarkets().findFirst().get();

        Object[] items = market.searchTradedItems().toArray();
        final int itemLineHeight = Font.CHAR_HEIGHT + 1;
        final int itemCol = 5;
        final int sellCol = 180;
        final int buyCol = 260;
        writeString("ITEM", itemCol, 5);
        writeString("SELL", sellCol, 5);
        writeString("BUY", buyCol, 5);
        for (int i = 0, maxI = Math.min(items.length, 15); i < maxI; ++i) {
            writeString(items[i].toString(), itemCol, 5 + (i+1) * itemLineHeight);
            writeString(market.searchBestSellingTrade((Item) items[i]).get().getPrice().toString(), sellCol, 5 + (i+1) * itemLineHeight);
            writeString(market.searchBestBuyingTrade((Item) items[i]).get().getPrice().toString(), buyCol, 5 + (i+1) * itemLineHeight);
        }
    }
}
