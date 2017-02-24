package ch.chrummibei.silvercoin.gui.widgets;

import ch.chrummibei.silvercoin.universe.components.NamedComponent;
import ch.chrummibei.silvercoin.universe.components.TraderComponent;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.entity_systems.FactorySystem;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import ch.chrummibei.silvercoin.universe.position.YieldingItemPosition;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

/**
 * Created by brachiel on 24/02/2017.
 */
public class FactoryListEntry extends HorizontalGroup {
    private final Label nameLabel;
    private final Label amountLabel;
    private final Label priceLabel;
    private final Entity entity;

    public FactoryListEntry(Entity entity, Skin skin) {
        super();

        this.entity = entity;
        nameLabel = new Label("", skin);
        amountLabel = new Label("", skin);
        priceLabel = new Label("", skin);

        nameLabel.setAlignment(Align.left);
        amountLabel.setAlignment(Align.right);
        priceLabel.setAlignment(Align.right);

        updateLabels();

        addActor(nameLabel);
        addActor(amountLabel);
        addActor(priceLabel);
    }

    public void updateLabels() {
        NamedComponent named = Mappers.named.get(entity);
        TraderComponent trader = Mappers.trader.get(entity);
        YieldingItemPosition productPosition = FactorySystem.getProductPosition(entity);

        nameLabel.setText(named.name);
        amountLabel.setText(String.valueOf(productPosition.getAmount()));
        priceLabel.setText(trader.ownTradeOffers.stream()
                .filter(offer -> offer.getItem() == productPosition.getItem() && offer.getAmount() > 0)
                .findAny()
                .map(TradeOffer::getPrice)
                .map(Price::toString)
                .orElse("-"));

        nameLabel.invalidate();
        amountLabel.invalidate();
    }

    @Override
    public void act(float delta) {
        updateLabels();
        super.act(delta);
    }
}
