package ch.chrummibei.silvercoin.universe.entity_systems;

import ch.chrummibei.silvercoin.universe.components.BigSpenderComponent;
import ch.chrummibei.silvercoin.universe.components.TradeSphereComponent;
import ch.chrummibei.silvercoin.universe.components.TraderComponent;
import ch.chrummibei.silvercoin.universe.components.WalletComponent;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

/**
 * Created by brachiel on 21/02/2017.
 */
public class BigSpenderSystem extends IteratingSystem {
    private static Family family = Family.all(
            BigSpenderComponent.class,
            TradeSphereComponent.class,
            TraderComponent.class,
            WalletComponent.class).get();
    public BigSpenderSystem() {
        super(family);
    }

    public BigSpenderSystem(int priority) {
        super(family, priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BigSpenderComponent bigSpender = Mappers.bigSpender.get(entity);
        bigSpender.timeReservoir += deltaTime;

        buyItems(entity);
    }

    public static boolean buyItems(Entity entity) {
        BigSpenderComponent bigSpender = Mappers.bigSpender.get(entity);
        TradeSphereComponent tradeSphere = Mappers.tradeSphere.get(entity);
        WalletComponent wallet = Mappers.wallet.get(entity);

        int itemsToBuy = (int) Math.floor(bigSpender.timeReservoir / bigSpender.consumptionTime);
        if (itemsToBuy == 0) {
            return false;
        }

        TradeOffer.searchTradeOffersToTradeAmount(
                tradeSphere.getAllTrades(),
                bigSpender.itemToConsume,
                TradeOffer.TYPE.SELLING,
                itemsToBuy)
            .forEach((offer, amount) -> {
                // Generate money for the BigSpender out of nothing
                wallet.credit.iAdd(offer.getPrice().toTotalValue(amount));

                offer.accept(entity, amount);
            });

        return true;
    }
}
