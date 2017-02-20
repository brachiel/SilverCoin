package ch.chrummibei.silvercoin.universe.entity_systems;

import ch.chrummibei.silvercoin.universe.components.InventoryComponent;
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

    public TraderSystem(Family family) {
        super(family);
    }

    public TraderSystem(Family family, int priority) {
        super(family, priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TraderComponent trader = Mappers.trader.get(entity);
        WalletComponent wallet = Mappers.wallet.get(entity);
        InventoryComponent inventory = Mappers.inventory.get(entity);

        processTrades(trader, wallet, inventory);
    }

    public void processTrades(TraderComponent trader, WalletComponent wallet, InventoryComponent inventory) {
        for (Trade trade : trader.acceptedTrades) {
            try {
                PricedItemPosition newItemPosition = trade.getTradersItemPosition(trader);
                wallet.credit.iSubtract(newItemPosition.getPurchaseValue());
                addPricedPositionToInventory(inventory, newItemPosition);

                // Reduce the offer if this was my trade
                if (trader.ownTradeOffers.contains(trade.fromTradeOffer)) {
                    trade.fromTradeOffer.reduceAmountBy(trade.getAmount());

                    if (trade.fromTradeOffer.getAmount() <= 0) {
                        trader.ownTradeOffers.remove(trade.fromTradeOffer);
                    }
                }
            } catch (TraderNotInvolvedException e) {
                e.printStackTrace();
            }
        }

        // Remove all accepted trades
        trader.acceptedTrades.clear();
    }

    public void addPricedPositionToInventory(InventoryComponent inventoryComponent, PricedItemPosition inventoryItem) {
        Map<Item, YieldingItemPosition> inventory = inventoryComponent.inventory;
        if (inventoryItem.getAmount() == 0) {
            throw new RuntimeException("Trying to add a position with amount 0. This is a bug.");
        }

        if (inventory.containsKey(inventoryItem.getItem())) {
            inventory.get(inventoryItem.getItem()).add(inventoryItem);
            if (inventory.get(inventoryItem.getItem()).getAmount() < 0) {
                throw new RuntimeException("We have negative positions. This shouldn't happen (yet). This is a bug.");
            }
        } else {
            inventory.put(inventoryItem.getItem(), new YieldingItemPosition(inventoryItem));
        }
    }

    public static Stream<TradeOffer> getOwnTradeOffers(TraderComponent trader, Item item, TradeOffer.TYPE type) {
        return trader.ownTradeOffers.stream().filter(offer -> offer.getItem() == item && offer.getType() == type);
    }
}
