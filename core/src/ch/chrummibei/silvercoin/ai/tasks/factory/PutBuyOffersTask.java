package ch.chrummibei.silvercoin.ai.tasks.factory;

import ch.chrummibei.silvercoin.universe.components.FactoryComponent;
import ch.chrummibei.silvercoin.universe.components.TraderComponent;
import ch.chrummibei.silvercoin.universe.entity_systems.FactorySystem;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import ch.chrummibei.silvercoin.universe.entity_systems.TraderSystem;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by brachiel on 23/02/2017.
 */
public class PutBuyOffersTask extends LeafTask<Entity> {
    @Override
    public Status execute() {
        FactoryComponent factory = Mappers.factory.get(this.getObject());
        TraderComponent trader = Mappers.trader.get(this.getObject());

        Map<Item,Integer> futureInventory = TraderSystem.correctInventoryWithAcceptedTrades(this.getObject());

        factory.recipe.ingredients.keySet().forEach(item -> {
            int stockAmount = futureInventory.get(item);
            int amountToBuy = FactorySystem.calcNeededIngredientAmount(this.getObject(), item) - stockAmount;

            Stream<TradeOffer> myTradeOffers = TraderSystem.getOwnTradeOffers(trader, item, TradeOffer.TYPE.BUYING);
            if (amountToBuy > 0) {
                myTradeOffers.forEach(offer -> offer.updateAmount(amountToBuy));
            } else {
                trader.ownTradeOffers.removeAll(myTradeOffers.collect(Collectors.toList()));
            }
        });

        return Status.SUCCEEDED;
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        return task;
    }
}
