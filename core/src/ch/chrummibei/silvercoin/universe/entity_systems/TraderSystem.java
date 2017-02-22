package ch.chrummibei.silvercoin.universe.entity_systems;

import ch.chrummibei.silvercoin.universe.components.InventoryComponent;
import ch.chrummibei.silvercoin.universe.components.NamedComponent;
import ch.chrummibei.silvercoin.universe.components.TraderComponent;
import ch.chrummibei.silvercoin.universe.components.WalletComponent;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.position.PricedItemPosition;
import ch.chrummibei.silvercoin.universe.position.YieldingItemPosition;
import ch.chrummibei.silvercoin.universe.trade.Trade;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import ch.chrummibei.silvercoin.universe.trade.TraderNotInvolvedException;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by brachiel on 20/02/2017.
 */
public class TraderSystem extends IteratingSystem {
    private static final Family family = Family.all(TraderComponent.class,
                                                    WalletComponent.class,
                                                    InventoryComponent.class).get();

    public TraderSystem() {
        super(family);
    }

    public TraderSystem(int priority) {
        super(family, priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TraderComponent trader = Mappers.trader.get(entity);
        if (trader.acceptedTrades.size() > 0) {
            processTrades(entity);
        }

        if (trader.ownTradeOffers.size() > 0) {
            removeOwnEmptyTradeOffers(entity);
        }
    }

    public static void removeOwnEmptyTradeOffers(Entity entity) {
        TraderComponent trader = Mappers.trader.get(entity);
        trader.ownTradeOffers.removeIf(offer -> offer.getAmount() == 0);
    }

    public static void processTrades(Entity entity) {
        TraderComponent trader = Mappers.trader.get(entity);
        WalletComponent wallet = Mappers.wallet.get(entity);

        // The amount in trade offers is decreased automatically by the trade offer upon accepting
        for (Trade trade : trader.acceptedTrades) {
            System.out.println(Mappers.named.get(entity).name + " processing accepted " + trade);
            System.out.println("Before process: " + Mappers.inventory.get(entity).positions.get(trade.getItem()));
            trader.ownTradeOffers.forEach(offer ->
                    System.out.println("   " + offer)
            );

            try {
                PricedItemPosition newItemPosition = trade.getTradersItemPosition(entity);
                wallet.credit.iSubtract(newItemPosition.getPurchaseValue());
                addPricedPositionToInventory(entity, newItemPosition);
            } catch (TraderNotInvolvedException e) {
                e.printStackTrace();
            }

            System.out.println("After process: " + Mappers.inventory.get(entity).positions.get(trade.getItem()));
        }

        // Remove all accepted trades
        trader.acceptedTrades.clear();
    }

    public static void addPricedPositionToInventory(Entity entity, PricedItemPosition inventoryItem) {
        InventoryComponent inventory = Mappers.inventory.get(entity);

        if (inventoryItem.getAmount() == 0) {
            throw new RuntimeException("Trying to add a position with amount 0. This is a bug.");
        }

        if (inventory.positions.containsKey(inventoryItem.getItem())) {
            inventory.positions.get(inventoryItem.getItem()).add(inventoryItem);
            if (inventory.positions.get(inventoryItem.getItem()).getAmount() < 0) {
                throw new RuntimeException("We have negative positions. This shouldn't happen (yet). This is a bug.");
            }
        } else {
            inventory.positions.put(inventoryItem.getItem(), new YieldingItemPosition(inventoryItem));
        }
    }

    public static Stream<TradeOffer> getOwnTradeOffers(TraderComponent trader, Item item, TradeOffer.TYPE type) {
        return trader.ownTradeOffers.stream().filter(offer -> offer.getItem() == item && offer.getType() == type);
    }
}
