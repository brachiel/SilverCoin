package ch.chrummibei.silvercoin.universe.trade;

import ch.chrummibei.silvercoin.universe.credit.TotalValue;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.item.Recipe;
import ch.chrummibei.silvercoin.universe.position.YieldingItemPosition;

/**
 * A Factory is a trader who is able to convert Items into a specific product.
 * Each Factory can only produce a single item.
 */
public class Factory extends Trader {


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

}
