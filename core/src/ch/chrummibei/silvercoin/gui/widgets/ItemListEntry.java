package ch.chrummibei.silvercoin.gui.widgets;

import ch.chrummibei.silvercoin.universe.components.MarketComponent;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.entity_systems.MarketUtil;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by brachiel on 24/02/2017.
 */
public class ItemListEntry extends HorizontalGroup {
    private final Item item;
    private final MarketComponent market;
    private final Label nameLabel;
    private final Label sellPriceLabel;
    private final Label buyPriceLabel;

    public ItemListEntry(Item item, MarketComponent market, Skin skin) {
        super();
        this.item = item;
        this.market = market;

        nameLabel = new Label(item.getName(), skin);
        sellPriceLabel = new Label("", skin);
        buyPriceLabel = new Label("", skin);

        updateLabels();

        addActor(nameLabel);
        addActor(sellPriceLabel);
        addActor(buyPriceLabel);
    }

    public void updateLabels() {
        sellPriceLabel.setText(MarketUtil.searchBestSellingTrade(market, item)
                .map(TradeOffer::getPrice)
                .map(Price::toString).orElse("-"));
        buyPriceLabel.setText(MarketUtil.searchBestBuyingTrade(market, item)
                .map(TradeOffer::getPrice)
                .map(Price::toString).orElse("-"));
    }

    @Override
    public void act(float delta) {
        updateLabels();
        super.act(delta);
    }
}
