package ch.chrummibei.silvercoin.gui.hud;

import ch.chrummibei.silvercoin.universe.components.FactoryComponent;
import ch.chrummibei.silvercoin.universe.components.TraderComponent;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.entity_systems.FactorySystem;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

/**
 * Created by brachiel on 03/03/2017.
 */
public class BottomBar extends Table {
    private Entity factory;
    private final Label factoryName;
    private final Label product;
    private final Label stock;
    private final Label sellPrice;
    private FactoryComponent factoryComponent;
    private TraderComponent traderComponent;

    public BottomBar(Skin skin) {
        factoryName = new Label("", skin);
        product = new Label("", skin);
        stock = new Label("", skin);
        sellPrice = new Label("", skin);

        align(Align.left);
        pad(0, 15, 0, 15);
        add(factoryName);
        add(product).padLeft(15);
        add(stock).align(Align.right).padLeft(15);
        add(sellPrice).align(Align.right).padLeft(15);
    }

    public void setDisplayedFactory(Entity entity) {
        factory = entity;
        factoryComponent = Mappers.factory.get(entity);
        traderComponent = Mappers.trader.get(entity);

        factoryName.setText("FACTORY: " + Mappers.named.get(entity).name);
        product.setText("PRODUCT: " + factoryComponent.recipe.product.getName());
    }

    public void clearDisplayedFactory() {
        factory = null;
        factoryName.setText("");
        product.setText("");
        stock.setText("");
        sellPrice.setText("");
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (factory == null) return;
        sellPrice.setText("PRICE: " +
            traderComponent.filterTradeOffers(factoryComponent.recipe.product, TradeOffer.TYPE.SELLING)
                    .findAny().map(TradeOffer::getPrice).map(Price::toString).orElse("-")
        );
        stock.setText("STOCK: " +
                String.format("%1$d / %2$d",
                        FactorySystem.getProductPosition(factory).getAmount(),
                        factoryComponent.goalStock)
        );
    }
}
