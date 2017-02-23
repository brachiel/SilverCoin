package ch.chrummibei.silvercoin.ai.tasks.factory;

import ch.chrummibei.silvercoin.universe.components.FactoryComponent;
import ch.chrummibei.silvercoin.universe.components.MarketSightComponent;
import ch.chrummibei.silvercoin.universe.components.TraderComponent;
import ch.chrummibei.silvercoin.universe.credit.InvalidPriceException;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.entity_systems.FactorySystem;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import ch.chrummibei.silvercoin.universe.entity_systems.TraderSystem;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
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
        MarketSightComponent marketSight = Mappers.marketSight.get(this.getObject());

        int availableProductAmount = TraderSystem.correctAmountWithAcceptedTrades(this.getObject(), factory.recipe.product);
        Price price;
        try {
            price = FactorySystem.getProductPosition(this.getObject()).getPurchasePrice().multiply(factory.priceSpreadFactor);
        } catch (InvalidPriceException e) {
            e.printStackTrace();
            return Status.FAILED;
        }

        // TODO: This is ugly and doesn't work if we don't copy trade offers to new marketSights.
        if (trader.ownTradeOffers.stream().anyMatch(offer -> offer.getItem() == factory.recipe.product && offer.getType() == TradeOffer.TYPE.SELLING)) {
            trader.ownTradeOffers.forEach(offer -> {
                offer.updateAmount(availableProductAmount);
                offer.getPrice().set(price);
            });
        } else {
            TradeOffer myNewTrade = new TradeOffer(this.getObject(), factory.recipe.product, TradeOffer.TYPE.SELLING, availableProductAmount, price);
            trader.ownTradeOffers.add(myNewTrade);
            marketSight.markets.forEach(market -> market.offeredTrades.add(myNewTrade));
        }

        return Status.SUCCEEDED;
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        return task;
    }
}
