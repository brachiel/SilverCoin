package ch.chrummibei.silvercoin.universe.item;

import java.util.Map;

/**
 * A recipe defined a set and amount of ingredient items and a product. Recipes are used by factories to produce items
 * from other items.
 */
public class Recipe {
    public final Item product;
    public final Map<Item,Integer> ingredients;

    public Recipe(Item product, Map<Item, Integer> ingredients) {
        this.product = product;
        this.ingredients = ingredients;
    }

    public int getIngredientAmount(Item item) {
        return ingredients.get(item);
    }
}
