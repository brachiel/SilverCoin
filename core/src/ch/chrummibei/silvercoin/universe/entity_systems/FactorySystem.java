package ch.chrummibei.silvercoin.universe.entity_systems;

import ch.chrummibei.silvercoin.universe.components.FactoryComponent;
import ch.chrummibei.silvercoin.universe.components.InventoryComponent;
import ch.chrummibei.silvercoin.universe.components.MarketSightComponent;
import ch.chrummibei.silvercoin.universe.components.TraderComponent;
import ch.chrummibei.silvercoin.universe.credit.InvalidPriceException;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.position.YieldingItemPosition;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import ch.chrummibei.silvercoin.universe.trade.TradeOfferHasNotEnoughAmountLeft;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

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
        if (produceProduct(entity, deltaTime)) {
            // Was unable to produce product due to missing ingredients.
            updateSellTrade(entity);

        } else if (! buyIngredients(entity)) {
            // Was unable to buy any ingredients. Put buy offers on the markets.
            putBuyOffers(entity);

        }
    }

    private void updateSellTrade(Entity entity) {
        FactoryComponent factory = Mappers.factory.get(entity);
        TraderComponent trader = Mappers.trader.get(entity);
        MarketSightComponent marketSight = Mappers.marketSight.get(entity);

        int availableProductAmount = TraderSystem.correctAmountWithAcceptedTrades(entity, factory.recipe.product);
        Price price;
        try {
            price = getProductPosition(entity).getPurchasePrice().multiply(factory.priceSpreadFactor);
        } catch (InvalidPriceException e) {
            e.printStackTrace();
            return;
        }

        // TODO: This is ugly and doesn't work if we don't copy trade offers to new marketSights.
        if (trader.ownTradeOffers.stream().anyMatch(offer -> offer.getItem() == factory.recipe.product && offer.getType() == TradeOffer.TYPE.SELLING)) {
            trader.ownTradeOffers.forEach(offer -> {
                offer.updateAmount(availableProductAmount);
                offer.getPrice().set(price);
            });
        } else {
            TradeOffer myNewTrade = new TradeOffer(entity, factory.recipe.product, TradeOffer.TYPE.SELLING, availableProductAmount, price);
            trader.ownTradeOffers.add(myNewTrade);
            marketSight.markets.forEach(market -> market.offeredTrades.add(myNewTrade));
        }
    }


    public boolean produceProduct(Entity entity, float deltaTime) {
        FactoryComponent factory = Mappers.factory.get(entity);
        InventoryComponent inventory = Mappers.inventory.get(entity);
        
        int producingAmount = calcProducibleAmountWithIngredients(entity);
        // Do not produce more than needed
        producingAmount = Math.min(producingAmount,
                                   factory.goalStock - getProductPosition(entity).getAmount());

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

        // Calculate product price before we start to remove items or else the price may become invalid.
        Price productPrice = calcProductPriceFromPurchasePrice(entity);
        for (Item ingredient : factory.recipe.ingredients.keySet()) {
            int ingredientAmount = factory.recipe.getIngredientAmount(ingredient);
            YieldingItemPosition position = inventory.positions.get(ingredient);

            try {
                position.removeItems(producingAmount * ingredientAmount, position.getPurchasePrice());
            } catch (InvalidPriceException e) {
                e.printStackTrace();
            }
        }

        // Add the produced products
        getProductPosition(entity).addItems(producingAmount, productPrice);

        return true;
    }

    public static Price calcProductPriceFromPurchasePrice(Entity entity) {
        FactoryComponent factory = Mappers.factory.get(entity);
        InventoryComponent inventory = Mappers.inventory.get(entity);

        Price productPrice = new Price(factory.recipe.buildTimeMillis / 10); // Minimum price depends on buildTime
        factory.recipe.ingredients.forEach((ingredient, ingredientAmount) -> {
            YieldingItemPosition position = inventory.positions.get(ingredient);
            if (position.getAmount() == 0) {
                return;
            }

            try {
                productPrice.iAdd(position.getPurchasePrice().toTotalValue(ingredientAmount));
            } catch (InvalidPriceException e) {
                e.printStackTrace();
            }
        });

        return productPrice;
    }

    public static YieldingItemPosition getProductPosition(Entity entity) {
        FactoryComponent factory = Mappers.factory.get(entity);
        InventoryComponent inventory = Mappers.inventory.get(entity);

        return inventory.positions.get(factory.recipe.product);
    }

    public int calcProducibleAmountWithIngredients(Entity entity) {
        FactoryComponent factory =  Mappers.factory.get(entity);
        InventoryComponent inventory = Mappers.inventory.get(entity);

        if (factory.recipe.ingredients.size() == 0) { // This recipe needs no ingredients. Production only depends on time
            return Integer.MAX_VALUE;
        }

        return factory.recipe.ingredients.keySet().stream()
                .mapToInt(item -> calcProducibleAmount(entity, item))
                .min()
                .orElse(0);
    }

    private int calcProducibleAmount(Entity entity, Item item) {
        FactoryComponent factory =  Mappers.factory.get(entity);
        InventoryComponent inventory = Mappers.inventory.get(entity);

        return inventory.positions.get(item).getAmount() / factory.recipe.ingredients.get(item);
    }


    public boolean buyIngredients(Entity entity) {
        FactoryComponent factory = Mappers.factory.get(entity);
        InventoryComponent inventory = Mappers.inventory.get(entity);
        MarketSightComponent marketSight = Mappers.marketSight.get(entity);

        boolean acceptedATrade = false;

        for (Map.Entry<Item,Integer> entry : factory.recipe.ingredients.entrySet()) {
            int amountToBuy = entry.getValue() * factory.goalStock
                                - inventory.positions.get(entry.getKey()).getAmount();
            if (amountToBuy <= 0) continue;

            // Find the cheapest TradeOffers

            Map<TradeOffer, Integer> tradeOffers = MarketUtil.getTradeOffersToTradeAmount(
                    marketSight,
                    entry.getKey(),
                    TradeOffer.TYPE.SELLING,
                    amountToBuy);

            tradeOffers.forEach((offer, amount) -> {
                try {
                    offer.accept(entity, amount);
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

    private void putBuyOffers(Entity entity) {
        FactoryComponent factory = Mappers.factory.get(entity);
        TraderComponent trader = Mappers.trader.get(entity);

        Map<Item,Integer> futureInventory = TraderSystem.correctInventoryWithAcceptedTrades(entity);

        factory.recipe.ingredients.keySet().forEach(item -> {
            int stockAmount = futureInventory.get(item);
            int amountToBuy = calcNeededIngredientAmount(entity, item) - stockAmount;

            Stream<TradeOffer> myTradeOffers = TraderSystem.getOwnTradeOffers(trader, item, TradeOffer.TYPE.BUYING);
            if (amountToBuy > 0) {
                myTradeOffers.forEach(offer -> offer.updateAmount(amountToBuy));
            } else {
                trader.ownTradeOffers.removeAll(myTradeOffers.collect(Collectors.toList()));
            }
        });
    }

    public static int calcNeededIngredientAmount(Entity entity, Item item) {
        FactoryComponent factory = Mappers.factory.get(entity);
        return factory.recipe.ingredients.get(item) * factory.goalStock;
    }


}
