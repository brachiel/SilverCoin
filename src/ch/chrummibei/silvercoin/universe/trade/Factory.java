package ch.chrummibei.silvercoin.universe.trade;

import ch.chrummibei.silvercoin.universe.credit.InvalidPriceException;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.credit.TotalValue;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.item.Recipe;
import ch.chrummibei.silvercoin.universe.position.YieldingItemPosition;

import java.util.Map;
import java.util.Optional;

/**
 * A Factory is a trader who is able to convert Items to a specific CraftableItem.
 * Each Factory can only produce a single CraftableItem.
 */
public class Factory extends Trader {
    private static int factoryNameSequence = 0;

    private int goalStock;

    private final Recipe recipe;
    private final YieldingItemPosition productStock;
    //private final Price productPriceTemplate = new Price(1);

    private long timeReservoirMillis = 0;


    public Factory(Recipe recipe, int goalStock) {
        super(recipe.product.getName() + " factory " + String.valueOf(Factory.getNextFactoryNameSequence()));
        this.recipe = recipe;
        this.goalStock = goalStock;
        productStock = new YieldingItemPosition(recipe.product, 0, new TotalValue(0));

        inventory.put(recipe.product, productStock);
        // Initialise inventory with ingredients
        for (Item ingredient : recipe.ingredients.keySet()) {
            inventory.put(ingredient, new YieldingItemPosition(ingredient, 0, new TotalValue(0)));
        }
    }

    public Optional<Price> getProductPrice() {
        return getProductTradeOffer().map(TradeOffer::getPrice);
    }

    public Optional<TradeOffer> getProductTradeOffer() {
        return searchOfferedSellingTrades(recipe.product).findFirst();
    }

    public int getGoalStock() {
        return goalStock;
    }

    /**
     * Sets product price. Might be overwritten to allow for a profit.
     * @param price New Product price.
     */
    /*
    public void setProductPrice(Price price) {
        productPriceTemplate.set(price);
        getProductPrice().ifPresent(p -> p.set(price));
    }
    */

    private static int getNextFactoryNameSequence() {
        return factoryNameSequence++;
    }

    /**
     * Adds just produced product to the main trade offer.
     * @param amount of produced product
     * @param price of ingredients to produce this product
     */
    public void addProductToTradeOffer(int amount, Price price) {
        Optional<TradeOffer> offer = getProductTradeOffer();

        if (offer.isPresent()) {
            offer.get().addAmount(amount, price);
        } else {
            addTradeOffer(new TradeOffer(this, recipe.product, TradeOffer.TYPE.SELLING, amount, price));
        }
    }


    public int calcProducibleAmount(Item item) {
        return inventory.get(item).getAmount() / recipe.getIngredientAmount(item);
    }

    public int calcProducibleAmountWithIngredients() {
        if (recipe.ingredients.size() == 0) { // This recipe needs no ingredients. Production only depends on time
            return Integer.MAX_VALUE;
        }

        return recipe.ingredients.keySet().stream()
                                    .mapToInt(item -> calcProducibleAmount(item))
                                    .min()
                                    .orElse(0);
    }

    public int calcWantedAmountOfIngredient(Item ingredient) {
        return goalStock * recipe.ingredients.get(ingredient);
    }

    public Optional<TotalValue> calcTotalIngredientCostPerProductFromMarket(Market market) {
        TotalValue totalValue = new TotalValue(0);

        for (Map.Entry<Item, Integer> entry : recipe.ingredients.entrySet()) {
            Optional<TotalValue> buyCost = market.calculateTotalBuyCosts(entry.getKey(), entry.getValue());

            if (! buyCost.isPresent()) {
                // Item does not have a trade offer; we can't calculate a price
                return Optional.empty();
            } else {
                totalValue.iAdd(buyCost.get());
            }
        }

        return Optional.of(totalValue);
    }

    public void adaptPricesFor(Market market) {
        /*
        Optional<TotalValue> totalCostOfIngredients = calcTotalIngredientCostPerProductFromMarket(market);
        if (totalCostOfIngredients.isPresent()) {
            setProductPrice(totalCostOfIngredients.get().toPriceNotNull(1));
        }
        */

        for (Map.Entry<Item,Integer> entry : recipe.ingredients.entrySet()) {
            Item ingredient = entry.getKey();
            Optional<TradeOffer> bestOffer = market.searchBestBuyingTrade(ingredient);
            if (bestOffer.isPresent()) {
                setUniqueTradeOffer(ingredient, TradeOffer.TYPE.BUYING, entry.getValue() * goalStock,
                        bestOffer.get().getPrice().copy());
            }

        }
    }

    public void produceProduct(long availableTimeMillis) {
        int producingAmount = calcProducibleAmountWithIngredients();
        // Do not produce more than needed
        producingAmount = Math.min(producingAmount, goalStock - productStock.getAmount());

        if (producingAmount == 0) {
            // We don't have enough ingredients, so we can't start to produce. Resetting time reservoir.
            timeReservoirMillis = 0;
            return;
        }
        // We would have enough ingredients, but maybe we don't have enough time.
        timeReservoirMillis += availableTimeMillis;

        // Calculate how many we can produce with the time reservoir
        producingAmount = Math.min(producingAmount, Math.toIntExact(timeReservoirMillis / recipe.buildTimeMillis));
        timeReservoirMillis -= producingAmount * recipe.buildTimeMillis; // Subtract the time we needed from reservoir

        Price productPrice = new Price(recipe.buildTimeMillis / 10); // Minimum price depends on buildTime
        for (Item ingredient : recipe.ingredients.keySet()) {
            int ingredientAmount = recipe.getIngredientAmount(ingredient);
            YieldingItemPosition position = inventory.get(ingredient);

            try {
                // Add the ingredient price to the product price
                productPrice = productPrice.add(position.getPurchasePrice().toTotalValue(ingredientAmount));
                // Reduce inventory by amount needed to produce the product
                position.removeItems(producingAmount * ingredientAmount, position.getPurchasePrice());
            } catch (InvalidPriceException e) {
                throw new RuntimeException("Position has an amount of 0. This is a bug.");
            }
        }

        // Add the produced products
        productStock.addItems(producingAmount, productPrice);

        // Update trade offer
        addProductToTradeOffer(producingAmount, productPrice);
    }

    public int getProductStock() {
        return productStock.getAmount();
    }
}
