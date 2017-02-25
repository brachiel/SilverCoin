package ch.chrummibei.silvercoin.universe.entity_systems;

import ch.chrummibei.silvercoin.universe.components.*;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.position.PricedItemPosition;
import ch.chrummibei.silvercoin.universe.position.YieldingItemPosition;
import ch.chrummibei.silvercoin.universe.trade.Trade;
import ch.chrummibei.silvercoin.universe.trade.TradeNeed;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import java.util.Map;
import java.util.Optional;

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

        System.out.println("Before process");
        TraderSystem.logStatus(entity);
        System.out.println("---");

        if (trader.acceptedTrades.size() > 0) {
            processTrades(entity);
        }

        removeOwnEmptyTradeOffers(entity);
        removeEmptyTradeNeeds(entity);

        if (trader.tradeNeeds.size() > 0) {
            tradeAccordingToNeeds(entity);
        }

        if (Mappers.logger.has(entity)) logStatus(entity);
    }

    private void removeEmptyTradeNeeds(Entity entity) {
        TraderComponent trader = Mappers.trader.get(entity);
        trader.tradeNeeds.removeIf(need -> need.amount == 0);
    }

    private void tradeAccordingToNeeds(Entity entity) {
        TraderComponent trader = Mappers.trader.get(entity);

        for (TradeNeed need : trader.tradeNeeds) {
            int toTrade = acceptPossibleTrades(entity, need);
            if (toTrade == 0) continue;

            // Now, put an offer for the remaining trades
            putTradeOffer(entity, need, toTrade);
        }
    }

    private void putTradeOffer(Entity entity, TradeNeed need, int toTrade) {
        TraderComponent trader = Mappers.trader.get(entity);

        int toTradeAbs = Math.abs(toTrade);
        Optional<TradeOffer> existing = trader.filterTradeOffers(need.item, need.type()).findAny();
        if (existing.isPresent()) {
            if (need.priceLimit.isPresent()) {
                existing.get().addAmount(toTradeAbs, need.priceLimit.get());
            } else {
                existing.get().updateAmount(existing.get().getAmount() + toTradeAbs);
            }
        } else {
            TradeOffer.TYPE type = TradeOffer.TYPE.fromAmount(toTrade);
            need.priceLimit.ifPresent(price ->
                    trader.tradeOffers.add(new TradeOffer(entity,
                            need.item,
                            type,
                            toTradeAbs,
                            price))
            );
        }
    }


    /** Accept as many trades as possible, and return the amount that is left to trade */
    private int acceptPossibleTrades(Entity entity, TradeNeed need) {
        MarketComponent market = Mappers.market.get(entity);

        int toTrade = calcAmountToTrade(entity, need);
        if (toTrade == 0) return 0;

        TradeOffer.TYPE type = TradeOffer.TYPE.fromAmount(toTrade);
        int toTradeAbs = Math.abs(toTrade);

        for (Map.Entry<TradeOffer, Integer> entry :
                market.searchTradeOffersToTradeAmount(need.item, type.opposite(), toTradeAbs).entrySet()) {

            entry.getKey().accept(entity, entry.getValue());
            toTradeAbs -= entry.getValue();

            if (toTradeAbs < 0) {
                throw new RuntimeException("Less than 0 left. This is a bug.");
            }
        }

        return (toTrade > 0) ? toTradeAbs : -toTradeAbs;
    }

    public static int calcAmountToTrade(Entity entity, TradeNeed need) {
        InventoryComponent inventory = Mappers.inventory.get(entity);

        int amount = need.amount; // Can be positive or negative

        // Correct the amount by the accepted trades
        amount -= sumAcceptedTradeAmount(entity, need.item);

        // Correct the amount by already existing trade offers
        amount -= sumOwnTradeOfferAmount(entity, need.item);

        // Limit the amount by inventory if selling
        if (amount < 0) {
            amount = -Math.min(-amount, inventory.positions.get(need.item).getAmount());
        }

        return amount;
    }

    public static int sumOwnTradeOfferAmount(Entity entity, Item item) {
        TraderComponent trader = Mappers.trader.get(entity);
        return trader.tradeOffers.stream()
                    .filter(offer -> offer.getItem() == item)
                    .mapToInt(offer -> offer.isBuying() ? offer.getAmount() : -offer.getAmount())
                    .sum();
    }

    public static int sumAcceptedTradeAmount(Entity entity, Item item) {
        TraderComponent trader = Mappers.trader.get(entity);
        return trader.acceptedTrades.stream()
                .filter(trade -> trade.getItem() == item)
                .map(trade -> trade.getTradersItemPosition(entity))
                .mapToInt(PricedItemPosition::getAmount)
                .sum();
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
        System.out.println("  Trade Needs: ");
        trader.tradeNeeds.forEach(need -> System.out.println("    " + need));
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
            Item item = trade.getItem();

            PricedItemPosition newItemPosition = trade.getTradersItemPosition(entity);
            wallet.credit.iSubtract(newItemPosition.getPurchaseValue());
            addPricedPositionToInventory(entity, newItemPosition);
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
}
