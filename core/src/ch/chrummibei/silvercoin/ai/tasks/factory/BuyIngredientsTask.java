package ch.chrummibei.silvercoin.ai.tasks.factory;

import ch.chrummibei.silvercoin.universe.components.FactoryComponent;
import ch.chrummibei.silvercoin.universe.components.InventoryComponent;
import ch.chrummibei.silvercoin.universe.components.MarketComponent;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import ch.chrummibei.silvercoin.universe.trade.TradeOfferHasNotEnoughAmountLeft;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import java.util.Map;

/**
 * Created by brachiel on 23/02/2017.
 */
public class BuyIngredientsTask extends LeafTask<Entity> {
    @Override
    public Status execute() {
        FactoryComponent factory = Mappers.factory.get(this.getObject());
        InventoryComponent inventory = Mappers.inventory.get(this.getObject());
        MarketComponent market = Mappers.market.get(this.getObject());

        Status acceptedATrade = Status.FAILED;

        for (Map.Entry<Item,Integer> entry : factory.recipe.ingredients.entrySet()) {
            int amountToBuy = entry.getValue() * factory.goalStock
                    - inventory.positions.get(entry.getKey()).getAmount();
            if (amountToBuy <= 0) continue;

            // Find the cheapest TradeOffers

            Map<TradeOffer, Integer> tradeOffers = market.searchTradeOffersToTradeAmount(
                    entry.getKey(),
                    TradeOffer.TYPE.SELLING,
                    amountToBuy);

            tradeOffers.forEach((offer, amount) -> {
                try {
                    offer.accept(this.getObject(), amount);
                } catch (TradeOfferHasNotEnoughAmountLeft tradeOfferHasNotEnoughAmountLeft) {
                    tradeOfferHasNotEnoughAmountLeft.printStackTrace();
                }
            });

            if (tradeOffers.size() > 0) {
                acceptedATrade = Status.SUCCEEDED;
            }
        }

        return acceptedATrade;
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        return task;
    }
}
