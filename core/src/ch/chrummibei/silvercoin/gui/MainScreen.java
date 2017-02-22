package ch.chrummibei.silvercoin.gui;

import ch.chrummibei.silvercoin.config.Resources;
import ch.chrummibei.silvercoin.config.UniverseConfig;
import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.components.MarketComponent;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import ch.chrummibei.silvercoin.universe.entity_systems.MarketUtil;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.ashley.core.Entity;
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


        MarketComponent market = universe.getMarketComponents().findFirst().get();

        TableWidget factoryTable = new TableWidget(font);
        factoryTable.addColumn("FACTORY", 180, Color.FIREBRICK);
        factoryTable.addColumn("STOCK", 50, TableWidget.STYLE.RIGHT_ALIGN);
        factoryTable.addColumn("PRICE", 90, TableWidget.STYLE.RIGHT_ALIGN);
        factoryTable.get(0).setRowHeight(factoryTable.defaultLineHeight() + 5);
        factoryTable.get(0).setColor(Color.CHARTREUSE);

        for (Entity factory : universe.getFactories().stream().limit(50).collect(Collectors.toList())) {
            TableRow row = new TableRow();
            row.add(Mappers.named.get(factory).name);
            //TODO: row.add(String.valueOf(factory.getProductStock()));
            //TODO: row.add(factory.getProductPrice().map(Price::toString).orElse("-"));
            factoryTable.add(row);
        }

        TableWidget itemTable = new TableWidget(font);
        itemTable.addColumn("ITEM", 150, Color.SALMON);
        itemTable.addColumn("SELL", 80, TableWidget.STYLE.RIGHT_ALIGN, Color.FIREBRICK);
        itemTable.addColumn("BUY",80, TableWidget.STYLE.RIGHT_ALIGN, Color.GOLDENROD);
        itemTable.get(0).setRowHeight(factoryTable.defaultLineHeight() + 5);
        itemTable.get(0).setColor(Color.FIREBRICK);

        ArrayList<Item> items = universe.getItems();
        items.sort(Comparator.comparing(item -> item.getName()));
        for (Item item : items) {
            TableRow row = new TableRow();
            row.add(item.toString());
            row.add(MarketUtil.searchBestSellingTrade(market, item)
                    .map(TradeOffer::getPrice)
                    .map(Price::toString).orElse("-"));
            row.add(MarketUtil.searchBestBuyingTrade(market, item)
                    .map(TradeOffer::getPrice)
                    .map(Price::toString).orElse("-"));
            itemTable.add(row);
        }


        batch.begin();
        factoryTable.draw(batch, 5, HEIGHT-5);
        itemTable.draw(batch, (int) factoryTable.getWidth() + 20, HEIGHT-5);
        batch.end();

        universe.update(delta);
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
