package ch.chrummibei.silvercoin.gui;

import ch.chrummibei.silvercoin.config.UniverseConfig;
import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.trade.Factory;
import ch.chrummibei.silvercoin.universe.trade.Market;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Bitmap that represents the GUI that is shown to the user. Renders the universe to it. It is used by the
 * main Component to be painted on the Canvas.
 */
public class MainScreen implements Screen {
    private Universe universe;
    private UniverseConfig universeConfig;

    BitmapFont font = new BitmapFont();
    OrthogonalTiledMapRenderer renderer;

    private OrthographicCamera cam;
    private SpriteBatch batch;


    private void writeString(String string, int x, int y) {
        font.draw(batch, string, x, y);
    }


    @Override
    public void show() {
        universeConfig = new UniverseConfig();
        universe = new Universe(universeConfig);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Constructs a new OrthographicCamera, using the given viewport width and height
        // Height is multiplied by aspect ratio.
        cam = new OrthographicCamera(30, 30 * (h / w));

        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.update();

        batch = new SpriteBatch();
    }



    @Override
    public void render(float delta) {
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        Market market = universe.getMarkets().findFirst().get();

        final int lineHeight = Font.CHAR_HEIGHT + 1;

        final int itemNameCol = 5;
        final int sellCol = itemNameCol + 150;
        final int buyCol = sellCol + 100;

        int currentY = 5;

        writeString("ITEM", itemNameCol, currentY);
        writeString("SELL", sellCol, currentY);
        writeString("BUY", buyCol, currentY);

        ArrayList<Item> items = universe.getItems();
        items.sort(Comparator.comparing(item -> item.getName()));
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

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        cam.setToOrtho(false, 20 * width / height, 20);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
