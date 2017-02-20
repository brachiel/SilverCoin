package ch.chrummibei.silvercoin.universe.entity_systems;

import ch.chrummibei.silvercoin.universe.components.FactoryComponent;
import ch.chrummibei.silvercoin.universe.components.InventoryComponent;
import ch.chrummibei.silvercoin.universe.components.MarketSightComponent;
import ch.chrummibei.silvercoin.universe.components.TraderComponent;
import ch.chrummibei.silvercoin.universe.credit.InvalidPriceException;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.position.YieldingItemPosition;
import ch.chrummibei.silvercoin.universe.trade.Trade;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import ch.chrummibei.silvercoin.universe.trade.TradeOfferHasNotEnoughAmountLeft;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by brachiel on 20/02/2017.
 */
public class FactorySystem extends IteratingSystem {
    private static Family family = Family.all(FactoryComponent.class,
                                              MarketSightComponent.class,
                                              InventoryComponent.class,
                                              TraderComponent.class).get();
    public FactorySystem() {
        super(family);
    }

    public FactorySystem(int priority) {
        super(family, priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        FactoryComponent factory = Mappers.factory.get(entity);
        MarketSightComponent marketSight = Mappers.marketSight.get(entity);
        InventoryComponent inventory = Mappers.inventory.get(entity);
        TraderComponent trader = Mappers.trader.get(entity);

        if (! produceProduct(factory, inventory, deltaTime)) {
            // Was unable to produce product due to missing ingredients.
        } else if (! buyIngredients(factory, inventory, trader, marketSight)) {
            // Was unable to buy any ingredients. Put buy offers on the markets.
            putBuyOffers(factory, inventory, trader);
        }

        updateSellTrade(factory, inventory, trader, marketSight);
    }

    private void updateSellTrade(FactoryComponent factory,
                                 InventoryComponent inventory,
                                 TraderComponent trader,
                                 MarketSightComponent marketSight) {
        // Search all my product trade offers
        List<TradeOffer> myTradeOffers = TraderSystem.getOwnTradeOffers(
                trader, factory.recipe.product, TradeOffer.TYPE.SELLING).collect(Collectors.toList());

        // Delete all product sell offers from own offers and from all markets
        trader.ownTradeOffers.removeAll(myTradeOffers);
        marketSight.markets.forEach(market -> market.offeredTrades.removeAll(myTradeOffers));

        Price price;
        try {
            price = getProductPosition(factory, inventory).getPurchasePrice().multiply(factory.priceSpreadFactor);
        } catch (InvalidPriceException e) {
            price = new Price(100.0); // Fallback
        }

        // Add new trade offers to all markets
        int availableProductAmount = getProductPosition(factory, inventory).getAmount();
        TradeOffer newTradeOffer = new TradeOffer(trader, factory.recipe.product, TradeOffer.TYPE.SELLING, availableProductAmount, price);

        marketSight.markets.forEach(market -> market.offeredTrades.add(newTradeOffer));
        trader.ownTradeOffers.add(newTradeOffer);
    }

    public boolean produceProduct(FactoryComponent factory, InventoryComponent inventoryComponent, float deltaTime) {
        int producingAmount = calcProducibleAmountWithIngredients(factory, inventoryComponent);
        // Do not produce more than needed
        producingAmount = Math.min(producingAmount,
                                   factory.goalStock - getProductPosition(factory, inventoryComponent).getAmount());

        if (producingAmount == 0) {
            // We don't have enough ingredients, so we can't start to produce. Resetting time reservoir.
            factory.timeReservoirMillis = 0;
            return false;
        }
        // We would have enough ingredients, but maybe we don't have enough time.
        factory.timeReservoirMillis += deltaTime * 1000;

        // Calculate how many we can produce with the time reservoir
        producingAmount = Math.min(producingAmount,
                                   Math.toIntExact(factory.timeReservoirMillis / factory.recipe.buildTimeMillis));

        if (producingAmount == 0) {
            return false; // Not enough time has passed to produce.
        }

        factory.timeReservoirMillis -= producingAmount * factory.recipe.buildTimeMillis; // Subtract the time we needed from reservoir

        Price productPrice = new Price(factory.recipe.buildTimeMillis / 10); // Minimum price depends on buildTime
        for (Item ingredient : factory.recipe.ingredients.keySet()) {
            int ingredientAmount = factory.recipe.getIngredientAmount(ingredient);
            YieldingItemPosition position = inventoryComponent.inventory.get(ingredient);

            try {
                // Add the ingredient price to the product price
                productPrice.iAdd(position.getPurchasePrice().toTotalValue(ingredientAmount));
                // Reduce inventory by amount needed to produce the product
                position.removeItems(producingAmount * ingredientAmount, position.getPurchasePrice());
            } catch (InvalidPriceException e) {
                throw new RuntimeException("Position has an amount of 0. This is a bug.");
            }
        }

        // Add the produced products
        getProductPosition(factory, inventoryComponent).addItems(producingAmount, productPrice);

        return true;
    }

    private YieldingItemPosition getProductPosition(FactoryComponent factory, InventoryComponent inventoryComponent) {
        return inventoryComponent.inventory.get(factory.recipe.product);
    }


    public int calcProducibleAmountWithIngredients(FactoryComponent factory, InventoryComponent inventory) {
        if (factory.recipe.ingredients.size() == 0) { // This recipe needs no ingredients. Production only depends on time
            return Integer.MAX_VALUE;
        }

        return factory.recipe.ingredients.keySet().stream()
                .mapToInt(item -> calcProducibleAmount(factory, inventory, item))
                .min()
                .orElse(0);
    }

    private int calcProducibleAmount(FactoryComponent factory, InventoryComponent inventoryComponent, Item item) {
        return inventoryComponent.inventory.get(item).getAmount() / factory.recipe.ingredients.get(item);
    }


    public boolean buyIngredients(FactoryComponent factory,
                                  InventoryComponent inventoryComponent,
                                  TraderComponent trader,
                                  MarketSightComponent marketSight) {
        boolean acceptedATrade = false;

        for (Map.Entry<Item,Integer> entry : factory.recipe.ingredients.entrySet()) {
            int amountToBuy = entry.getValue() * factory.goalStock
                                - inventoryComponent.inventory.get(entry.getKey()).getAmount();
            if (amountToBuy <= 0) continue;

            // Find the cheapest TradeOffers

            Map<TradeOffer, Integer> tradeOffers = MarketUtil.getTradeOffersToTradeAmount(
                    marketSight,
                    entry.getKey(),
                    TradeOffer.TYPE.SELLING,
                    amountToBuy);

            tradeOffers.forEach((offer, amount) -> {
                try {
                    offer.accept(trader, amount);
                } catch (TradeOfferHasNotEnoughAmountLeft tradeOfferHasNotEnoughAmountLeft) {
                    tradeOfferHasNotEnoughAmountLeft.printStackTrace();
                }
            });

            if (tradeOffers.size() > 0) {
                acceptedATrade = true;
            }
        }

        return acceptedATrade;
    }

    private void putBuyOffers(FactoryComponent factory, InventoryComponent inventory, TraderComponent trader) {
        Map<Item,Integer> futureInventory = correctInventoryWithAcceptedTrades(inventory, trader);

        futureInventory.forEach((item, stockAmount) -> {
            int amountToBuy = calcNeededIngredientAmount(factory, item) - futureInventory.get(item);

            Stream<TradeOffer> myTradeOffers = TraderSystem.getOwnTradeOffers(trader, item, TradeOffer.TYPE.BUYING);
            if (amountToBuy > 0) {
                myTradeOffers.forEach(offer -> offer.updateAmount(amountToBuy));
            } else {
                trader.ownTradeOffers.removeAll(myTradeOffers.collect(Collectors.toList()));
            }
        });
    }

    private int calcNeededIngredientAmount(FactoryComponent factory, Item item) {
        return factory.recipe.ingredients.get(item) * factory.goalStock;
    }

    public Map<Item,Integer> correctInventoryWithAcceptedTrades(InventoryComponent inventoryComponent,
                                                                TraderComponent trader) {
        HashMap<Item,Integer> correctedInventory = new HashMap<>();
        inventoryComponent.inventory.forEach((item,position) -> correctedInventory.put(item,position.getAmount()));

        for (Trade trade : trader.acceptedTrades) {
            int oldAmount = correctedInventory.get(trade.getItem());
            if (trade.getBuyer() == trader) {
                // I am buying
                correctedInventory.put(trade.getItem(), oldAmount + trade.getAmount());
            } else {
                // I am selling
                correctedInventory.put(trade.getItem(), oldAmount - trade.getAmount());
            }
        }

        return correctedInventory;
    }

}
