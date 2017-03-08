package ch.chrummibei.silvercoin.gui.hud;

import ch.chrummibei.silvercoin.universe.components.FactoryComponent;
import ch.chrummibei.silvercoin.universe.components.TraderComponent;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by brachiel on 03/03/2017.
 */
public class BottomBar extends HorizontalGroup {
    private Entity factory;
    private final Label factoryName;
    private final Label product;
    private final Label sellPrice;
    private FactoryComponent factoryComponent;
    private TraderComponent traderComponent;

    public BottomBar(Skin skin) {
        factoryName = new Label("FACTORY", skin);
        product = new Label("PRODUCT", skin);
        sellPrice = new Label("SELLPRICE", skin);

        addActor(factoryName);
        addActor(product);
        addActor(sellPrice);
    }

    public void setDisplayedFactory(Entity entity) {
        factory = entity;
        factoryComponent = Mappers.factory.get(entity);
        traderComponent = Mappers.trader.get(entity);

        factoryName.setText(Mappers.named.get(entity).name);
        product.setText(factoryComponent.recipe.product.getName());
    }

    public void clearDisplayedFactory() {
        factory = null;
        factoryName.setText("");
        product.setText("");
        sellPrice.setText("");
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (factory == null) return;
        sellPrice.setText(
                traderComponent.filterTradeOffers(factoryComponent.recipe.product, TradeOffer.TYPE.SELLING)
                        .findAny().map(TradeOffer::getPrice).map(Price::toString).orElse("-")
        );
    }
}
