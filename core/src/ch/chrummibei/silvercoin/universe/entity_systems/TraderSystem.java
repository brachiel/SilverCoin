package ch.chrummibei.silvercoin.universe.entity_systems;

import ch.chrummibei.silvercoin.universe.components.*;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.position.PricedItemPosition;
import ch.chrummibei.silvercoin.universe.position.YieldingItemPosition;
import ch.chrummibei.silvercoin.universe.trade.Trade;
import ch.chrummibei.silvercoin.universe.trade.TraderNotInvolvedException;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brachiel on 20/02/2017.
 */
public class TraderSystem extends IteratingSystem {
    private static final Family family = Family.all(TraderComponent.class,
                                                    WalletComponent.class,
                                                    MarketComponent.class,
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

        if (Mappers.logger.has(entity)) logStatus(entity);

        if (trader.acceptedTrades.size() > 0) {
            processTrades(entity);
        }

        if (trader.tradeOffers.size() > 0) {
            removeOwnEmptyTradeOffers(entity);
        }

        if (Mappers.logger.has(entity)) logStatus(entity);
    }


    public static void logStatus(Entity entity) {
        TraderComponent trader = Mappers.trader.get(entity);
        InventoryComponent inventory = Mappers.inventory.get(entity);
        NamedComponent named = Mappers.named.get(entity);

        if (named != null) {
            System.out.println("Logging Entity: " + named.name);
        } else {
            System.out.println("Logging Entity: " + entity);
        }
        System.out.println("  Own Trades: ");
        trader.tradeOffers.forEach(offer -> System.out.println("    " + offer));
        System.out.println("  Accepted Trades: ");
        trader.acceptedTrades.forEach(trade -> System.out.println("    " + trade));
        System.out.println("  Inventory: ");
        inventory.positions.values().forEach(pos -> System.out.println("    " + pos.getAmount() + " " + pos.getItem()));
    }

    public static void removeOwnEmptyTradeOffers(Entity entity) {
        TraderComponent trader = Mappers.trader.get(entity);
        trader.tradeOffers.removeIf(offer -> offer.getAmount() == 0);
    }

    public static void processTrades(Entity entity) {
        TraderComponent trader = Mappers.trader.get(entity);
        WalletComponent wallet = Mappers.wallet.get(entity);

        // The amount in trade offers is decreased automatically by the trade offer upon accepting
        for (Trade trade : trader.acceptedTrades) {
            try {
                PricedItemPosition newItemPosition = trade.getTradersItemPosition(entity);
                wallet.credit.iSubtract(newItemPosition.getPurchaseValue());
                addPricedPositionToInventory(entity, newItemPosition);
            } catch (TraderNotInvolvedException e) {
                e.printStackTrace();
            }
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
                TraderSystem.logStatus(entity);
                throw new RuntimeException("We have negative positions. This shouldn't happen (yet). This is a bug.");
            }
        } else {
            inventory.positions.put(inventoryItem.getItem(), new YieldingItemPosition(inventoryItem));
        }
    }


    public static Map<Item,Integer> correctInventoryWithAcceptedTrades(Entity entity) {
        InventoryComponent inventory = Mappers.inventory.get(entity);
        TraderComponent trader = Mappers.trader.get(entity);

        HashMap<Item,Integer> correctedInventory = new HashMap<>();
        inventory.positions.forEach((item, position) -> correctedInventory.put(item,position.getAmount()));

        for (Trade trade : trader.acceptedTrades) {
            int oldAmount = correctedInventory.get(trade.getItem());
            if (trade.getBuyer() == entity) {
                // I am buying
                correctedInventory.put(trade.getItem(), oldAmount + trade.getAmount());
            } else if (trade.getSeller() == entity) {
                // I am selling
                correctedInventory.put(trade.getItem(), oldAmount - trade.getAmount());
            } else {
                throw new RuntimeException("I am not part of this trade. This is a bug");
            }
        }

        return correctedInventory;
    }


    public static int correctAmountWithAcceptedTrades(Entity entity, Item item) {
        InventoryComponent inventory = Mappers.inventory.get(entity);
        TraderComponent trader = Mappers.trader.get(entity);

        int availableAmount = 0;
        try {
            availableAmount = inventory.positions.get(item).getAmount();
        } catch (NullPointerException e) {}

        for (Trade trade : trader.acceptedTrades) {
            if (trade.getBuyer() == entity) {
                // I am buying
                availableAmount += trade.getAmount();
            } else if (trade.getSeller() == entity) {
                // I am selling
                availableAmount -= trade.getAmount();
            } else {
                throw new RuntimeException("I am not part of this trade. This is a bug");
            }
        }

        return availableAmount;
    }
}
