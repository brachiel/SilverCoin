package ch.chrummibei.silvercoin.gui.widgets;

import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.components.FactoryComponent;
import ch.chrummibei.silvercoin.universe.components.NamedComponent;
import ch.chrummibei.silvercoin.universe.components.TraderComponent;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.entity_systems.FactorySystem;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import ch.chrummibei.silvercoin.universe.position.YieldingItemPosition;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by brachiel on 24/02/2017.
 */
public class FactoryList extends Table {
    Universe universe;

    public FactoryList(Universe universe, Skin skin) {
        super(skin);
        this.universe = universe;

        add("FACTORY").pad(0,0,5,5).align(Align.left);
        add("STOCK").pad(0,0,5,5).align(Align.right).minWidth(40);
        add("GOAL").pad(0,0,5,5).align(Align.right).minWidth(40);
        add("PRICE").pad(0,0,5,0).align(Align.right).minWidth(80);

        for (Entity entity : universe.getFactories().stream().collect(Collectors.toList())) {
            row();

            NamedComponent named = Mappers.named.get(entity);
            FactoryComponent factory = Mappers.factory.get(entity);

            add(String.valueOf(named.name)).align(Align.left);
            add(" ").align(Align.right);
            add(String.valueOf(factory.goalStock)).align(Align.right);
            add(" ").align(Align.right);
        }

        updateLabels();
    }

    public void updateLabels() {
        Array<Cell> cells = this.getCells();

        int i = 3;
        for (Entity entity : universe.getFactories().stream().collect(Collectors.toList())) {
            TraderComponent trader = Mappers.trader.get(entity);
            YieldingItemPosition productPosition = FactorySystem.getProductPosition(entity);

            Optional<TradeOffer> productSellOffer = trader.ownTradeOffers.stream()
                                .filter(offer -> offer.getItem() == productPosition.getItem() && offer.getAmount() > 0)
                                .findAny();
            ++i;
            ((Label) cells.get(++i).getActor()).setText(productSellOffer
                                                        .map(TradeOffer::getAmount)
                                                        .map(String::valueOf)
                                                        .orElse("-"));
            ++i;
            ((Label) cells.get(++i).getActor()).setText(productSellOffer
                                                        .map(TradeOffer::getPrice)
                                                        .map(Price::toString)
                                                        .orElse("-"));
        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        updateLabels();
        super.draw(batch, parentAlpha);
    }
}

