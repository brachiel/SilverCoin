package ch.chrummibei.silvercoin.gui.hud;

import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.components.MarketComponent;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.stream.Collectors;

/**
 * Created by brachiel on 24/02/2017.
 */
public class TradeOfferList extends Table {
    private final Universe universe;
    public MarketComponent market;

    public TradeOfferList(Universe universe, Skin skin) {
        super(skin);
        this.universe = universe;
        this.market = null;

        add("ITEM").pad(0,0,5,5).align(Align.left);
        add("AMOUNT").pad(0,0,5,5).align(Align.right).minWidth(40);
        add("PRICE").pad(0,0,5,0).align(Align.right).minWidth(80);

        updateLabels();
    }

    public void updateLabels() {
        Array<Cell> cells = this.getCells();

        // Remove all cells from row 2
        if (cells.size > 3)
            cells.removeRange(3,cells.size-1);

        if (market == null) return;

        for (TradeOffer offer : market.collectTradeOffers().collect(Collectors.toList())) {
            row();

            add(String.valueOf(offer.getItem())).align(Align.left);
            add(String.valueOf(
                    (offer.getType() == TradeOffer.TYPE.BUYING) ? offer.getAmount() : -offer.getAmount()
            )).align(Align.right);
            add(String.valueOf(offer.getPrice())).align(Align.right);
        }

        invalidate();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        updateLabels();
        super.draw(batch, parentAlpha);
    }
}

