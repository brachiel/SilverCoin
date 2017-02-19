package ch.chrummibei.silvercoin.gui;

import ch.chrummibei.silvercoin.config.Resources;
import ch.chrummibei.silvercoin.config.UniverseConfig;
import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.trade.Factory;
import ch.chrummibei.silvercoin.universe.trade.Market;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * libGDX Screen rendering universe and HUD
 */
public class MainScreen implements Screen {
    private Universe universe;
    private UniverseConfig universeConfig;

    BitmapFont font = Resources.getDefaultFont();

    private OrthographicCamera cam;
    private SpriteBatch batch;
    private Sprite backgroundSprite = new Sprite(new Texture("images/ngc253.jpg"));

    public int WIDTH;
    public int HEIGHT;


    private void writeString(String string, int x, int y) {
        font.draw(batch, string, x, y);
    }


    @Override
    public void show() {
        universeConfig = new UniverseConfig();
        universe = new Universe(universeConfig);

        WIDTH = Gdx.graphics.getWidth()/2;
        HEIGHT = Gdx.graphics.getHeight()/2;

        // Constructs a new OrthographicCamera, using the given viewport width and height
        // Height is multiplied by aspect ratio.
        cam = new OrthographicCamera(WIDTH, HEIGHT);

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
        backgroundSprite.setCenter(WIDTH/2, HEIGHT/2);
        backgroundSprite.setColor(0.3f,0.3f,0.3f,1f);
        backgroundSprite.draw(batch);
        batch.end();

        batch.begin();

        Market market = universe.getMarkets().findFirst().get();

        final int lineHeight = (int) font.getLineHeight() + 1;
        int currentY = HEIGHT - lineHeight;

        final int factoryNameCol = 5;
        final int stockCol = factoryNameCol + 200;
        final int priceCol = stockCol + 50;


        writeString("FACTORY", factoryNameCol, currentY);
        writeString("STOCK", stockCol, currentY);
        writeString("PRICE", priceCol, currentY);

        for (Factory factory : universe.getFactories().stream().limit(50).collect(Collectors.toList())) {
            currentY -= lineHeight;
            writeString(factory.getName(), factoryNameCol, currentY);
            writeString(String.valueOf(factory.getProductStock()), stockCol, currentY);
            writeString(factory.getProductPrice().map(Price::toString).orElse("-"), priceCol, currentY);
        }

        final int itemNameCol = priceCol + 70;
        final int sellCol = itemNameCol + 150;
        final int buyCol = sellCol + 80;

        currentY = HEIGHT - lineHeight;

        font.setColor(1, 1, 1, 1);
        writeString("ITEM", itemNameCol, currentY);
        writeString("SELL", sellCol, currentY);
        writeString("BUY", buyCol, currentY);

        ArrayList<Item> items = universe.getItems();
        items.sort(Comparator.comparing(item -> item.getName()));
        for (Item item : items) {
            currentY -= lineHeight;
            writeString(item.toString(), itemNameCol, currentY);
            writeString(market.searchBestSellingTrade(item)
                    .map(TradeOffer::getPrice)
                    .map(Price::toString).orElse("-"), sellCol, currentY);
            writeString(market.searchBestBuyingTrade(item)
                    .map(TradeOffer::getPrice)
                    .map(Price::toString).orElse("-"), buyCol, currentY);
        }

        batch.end();

        universe.tick((long) Math.floor(100*delta));
    }

    @Override
    public void resize(int width, int height) {
        WIDTH = Gdx.graphics.getWidth()/2;
        HEIGHT = Gdx.graphics.getHeight()/2;
        cam.setToOrtho(false, WIDTH, HEIGHT);
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
