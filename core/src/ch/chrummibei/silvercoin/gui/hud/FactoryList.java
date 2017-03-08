package ch.chrummibei.silvercoin.gui.hud;

import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.components.TraderComponent;
import ch.chrummibei.silvercoin.universe.entity_systems.FactorySystem;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import ch.chrummibei.silvercoin.universe.position.YieldingItemPosition;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import java.util.Comparator;
import java.util.Optional;

/**
 * Created by brachiel on 24/02/2017.
 */
public class FactoryList extends Table {
    public FactoryList(Skin skin) {
        super(skin);
        updateLabels();
    }

    public void updateLabels() {
        Vector2 playerPosition = Mappers.physics.get(Universe.player).body.getPosition();

        // TODO: This is really nasty

        // Remove all cells
        clear();

        // Create header
        add("FACTORY").pad(0,0,5,5).align(Align.left);
        add("STOCK").pad(0,0,5,5).align(Align.right).minWidth(40);
        add("GOAL").pad(0,0,5,5).align(Align.right).minWidth(40);
        add("PRICE").pad(0,0,5,0).align(Align.right).minWidth(80);

        Mappers.tradeSphere.get(Universe.player).tradersInSphere.stream()
                .filter(Mappers.factory::has) // only factories
                .sorted(Comparator.comparing(
                        entity -> Mappers.physics.get(entity).body.getPosition().dst(playerPosition))) // Sort by dist
                .forEachOrdered(entity -> {
                    row();
                    TraderComponent trader = Mappers.trader.get(entity);
                    YieldingItemPosition productPosition = FactorySystem.getProductPosition(entity);

                    Optional<TradeOffer> productSellOffer = trader.tradeOffers.stream()
                                        .filter(offer -> offer.getItem() == productPosition.getItem()
                                                            && offer.getAmount() > 0)
                                        .findAny();
                    add(Mappers.named.get(entity).name); // FACTORY
                    add(productSellOffer.map(TradeOffer::getAmount) // AMOUNT
                            .map(String::valueOf)
                            .orElse("-"));
                    add(String.valueOf(Mappers.factory.get(entity).goalStock)); // GOAL
                    add(productSellOffer.map(TradeOffer::getPrice) // PRICE
                            .map(String::valueOf)
                            .orElse("-"));
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        updateLabels();
        super.draw(batch, parentAlpha);
    }
}

