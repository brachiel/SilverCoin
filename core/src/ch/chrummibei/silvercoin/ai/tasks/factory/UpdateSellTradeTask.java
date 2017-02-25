package ch.chrummibei.silvercoin.ai.tasks.factory;

import ch.chrummibei.silvercoin.universe.components.FactoryComponent;
import ch.chrummibei.silvercoin.universe.components.TraderComponent;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.entity_systems.FactorySystem;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import ch.chrummibei.silvercoin.universe.trade.TradeNeed;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

/**
 * Created by brachiel on 23/02/2017.
 */
public class UpdateSellTradeTask extends LeafTask<Entity> {

    @Override
    public Status execute() {
        FactoryComponent factory = Mappers.factory.get(this.getObject());
        TraderComponent trader = Mappers.trader.get(this.getObject());

        int availableProductAmount = FactorySystem.getProductPosition(this.getObject()).getAmount();
        if (availableProductAmount <= 0) return Status.FAILED;

        Price price = FactorySystem.getProductPosition(this.getObject())
                .getPurchasePrice()
                .multiply(factory.priceSpreadFactor);


        trader.setTradeNeed(
                new TradeNeed(factory.recipe.product, -availableProductAmount, price));

        return Status.SUCCEEDED;
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        return task;
    }
}
