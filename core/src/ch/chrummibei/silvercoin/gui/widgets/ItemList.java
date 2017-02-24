package ch.chrummibei.silvercoin.gui.widgets;

import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.components.MarketComponent;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by brachiel on 24/02/2017.
 */
public class ItemList extends Table {
    ArrayList<Item> items;
    MarketComponent market;

    public ItemList(Universe universe, Skin skin) {
        super(skin);

        add("ITEM").pad(0,0,5,5).align(Align.left);
        add("SELL").pad(0,0,5,5).align(Align.right).minWidth(40);
        add("BUY").pad(0,0,5,0).align(Align.right).minWidth(40);

        items = universe.getItems();
        items.sort(Comparator.comparing(Item::getName));
        market = universe.getMarketComponents().findAny().get();

        for (Item item : items) {
            row();

            add(item.getName()).align(Align.left);
            add("").align(Align.right);
            add("").align(Align.right);
        }
    }

    public void updateLabels() {
        Array<Cell> cells = this.getCells();

        int i = 2;
        for (Item item : items) {
            ++i;
            ((Label) cells.get(++i).getActor()).setText(
                    market.searchBestSellingTrade(item)
                        .map(TradeOffer::getPrice)
                        .map(Price::toString).orElse("-"));
            ((Label) cells.get(++i).getActor()).setText(
                    market.searchBestBuyingTrade(item)
                        .map(TradeOffer::getPrice)
                        .map(Price::toString).orElse("-"));
        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        updateLabels();
        super.draw(batch, parentAlpha);
    }
}
