package ch.chrummibei.silvercoin.universe.trade;

import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.credit.TotalValue;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.item.Recipe;
import ch.chrummibei.silvercoin.universe.position.PricedItemPosition;
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
    private Price productPrice;

    private Recipe recipe;
    private YieldingItemPosition productStock;
    private TradeOffer productSellTradeOffer;


    public Factory(Recipe recipe, int goalStock) {
        super(recipe.product.getName() + " factory " + String.valueOf(Factory.getNextFactoryNameSequence()));
        this.recipe = recipe;
        this.goalStock = goalStock;
        productPrice = new Price(1);
        productSellTradeOffer = new TradeOffer(this, recipe.product, TradeOffer.TYPE.SELLING, 0, productPrice);
        productStock = new YieldingItemPosition(recipe.product, 0);
    }

    /**
     * Sets product price. Might be overwritten to allow for a profit.
     * @param price New Product price.
     */
    public void setProductPrice(Price price) {
        productPrice.set(price);
    }

    private static int getNextFactoryNameSequence() {
        return factoryNameSequence++;
    }

    /**
     * Adds just produced product to the main trade offer.
     * @param amount of produced product
     * @param price of ingredients to produce this product
     */
    public void addProductToTradeOffer(int amount, Price price) {
        productSellTradeOffer.addAmount(amount, price);
        if (! offeredTrades.contains(productSellTradeOffer)) {
            offeredTrades.add(productSellTradeOffer);
        }
    }

    public int calcProducibleAmount(Item item, int ownedAmount) {
        return ownedAmount / recipe.getIngredientAmount(item);
    }

    public int calcProducibleAmount() {
        return inventory.values().stream()
                .mapToInt(pos -> calcProducibleAmount(pos.getItem(), pos.getAmount()))
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
        Optional<TotalValue> totalCostOfIngredients = calcTotalIngredientCostPerProductFromMarket(market);
        if (totalCostOfIngredients.isPresent()) {
            setProductPrice(totalCostOfIngredients.get().toPrice(1));
            setUniqueTradeOffer(productStock.getItem(), TradeOffer.TYPE.SELLING, productStock.getAmount(), productPrice);
        }

        // TODO update ingredient offers
    }

    public void produceProduct() {
        int producingAmount = calcProducibleAmount();
        Price productPrice = new Price(0);
        for (PricedItemPosition position : inventory.values()) {
            int ingredientAmount = recipe.getIngredientAmount(position.getItem());
            // Add the ingredient price to the product price
            productPrice = productPrice.add(position.getPurchasePrice().toTotalValue(ingredientAmount));
            // Reduce inventory by amount needed to produce the product
            position.removeItems(producingAmount * ingredientAmount, position.getPurchasePrice());
        }

        // Add the produced products
        productStock.addItems(producingAmount, productPrice);

        // Update trade offer
        addProductToTradeOffer(producingAmount, productPrice);
    }
}
