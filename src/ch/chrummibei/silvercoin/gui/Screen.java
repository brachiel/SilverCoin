package ch.chrummibei.silvercoin.gui;

import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.trade.Factory;
import ch.chrummibei.silvercoin.universe.trade.Market;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;

import java.awt.image.DataBufferInt;
import java.util.List;
import java.util.stream.Collectors;

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

        List<Item> items = market.searchTradedItems().collect(Collectors.toList());
        final int lineHeight = Font.CHAR_HEIGHT + 1;
        final int nameCol = 5;
        final int sellCol = 180;
        final int buyCol = 260;

        int currentY = 5;

        writeString("ITEM", nameCol, currentY);
        writeString("SELL", sellCol, currentY);
        writeString("BUY", buyCol, currentY);

        for (Item item : items) {
            currentY += lineHeight;
            writeString(item.toString(), nameCol, currentY);
            writeString(market.searchBestSellingTrade(item)
                    .map(TradeOffer::getPrice)
                    .map(Price::toString).orElse("-"), sellCol, currentY);
            writeString(market.searchBestBuyingTrade(item)
                    .map(TradeOffer::getPrice)
                    .map(Price::toString).orElse("-"), buyCol, currentY);
        }

        final int stockCol = 240;
        final int priceCol = 360;

        currentY += 2 * lineHeight;

        writeString("FACTORY", nameCol, currentY);
        writeString("STOCK", stockCol, currentY);
        writeString("PRICE", priceCol, currentY);

        for (Factory factory : universe.getFactories().stream().limit(15).collect(Collectors.toList())) {
            currentY += lineHeight;
            writeString(factory.getName(), nameCol, currentY);
            writeString(String.valueOf(factory.getProductTradeOffer().getAmount()), stockCol, currentY);
            writeString(factory.getProductTradeOffer().getPrice().toString(), priceCol, currentY);
        }
    }
}
