package ch.chrummibei.silvercoin.gui;

import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.trade.Factory;
import ch.chrummibei.silvercoin.universe.trade.Market;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;

import java.awt.image.WritableRaster;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bitmap that represents the GUI that is shown to the user. Renders the universe to it. It is used by the
 * main Component to be painted on the Canvas.
 */
public class Screen extends Bitmap {
    public Screen(WritableRaster parentRaster) {
        super(parentRaster);
    }

    public void render(Universe universe) {
        // Clear Screen
        clearToTransparent();

        Market market = universe.getMarkets().findFirst().get();

        List<Item> items = market.searchTradedItems().collect(Collectors.toList());
        final int lineHeight = Font.CHAR_HEIGHT + 1;

        final int itemNameCol = 5;
        final int sellCol = itemNameCol + 150;
        final int buyCol = sellCol + 100;

        int currentY = 5;

        writeString("ITEM", itemNameCol, currentY);
        writeString("SELL", sellCol, currentY);
        writeString("BUY", buyCol, currentY);

        for (Item item : items) {
            currentY += lineHeight;
            writeString(item.toString(), itemNameCol, currentY);
            writeString(market.searchBestSellingTrade(item)
                    .map(TradeOffer::getPrice)
                    .map(Price::toString).orElse("-"), sellCol, currentY);
            writeString(market.searchBestBuyingTrade(item)
                    .map(TradeOffer::getPrice)
                    .map(Price::toString).orElse("-"), buyCol, currentY);
        }

        final int factoryNameCol = buyCol + 100;
        final int stockCol = factoryNameCol + 200;
        final int priceCol = stockCol + 100;

        currentY = 5;

        writeString("FACTORY", factoryNameCol, currentY);
        writeString("STOCK", stockCol, currentY);
        writeString("PRICE", priceCol, currentY);

        for (Factory factory : universe.getFactories().stream().limit(50).collect(Collectors.toList())) {
            currentY += lineHeight;
            writeString(factory.getName(), factoryNameCol, currentY);
            writeString(String.valueOf(factory.getProductStock()), stockCol, currentY);
            writeString(factory.getProductPrice().map(Price::toString).orElse("-"), priceCol, currentY);
        }
    }
}
