package ch.chrummibei.silvercoin.universe.item;

import java.util.Map;

/**
 * A recipe defined a set and amount of ingredient positions and a product. Recipes are used by factories to produce positions
 * from other positions.
 */
public class Recipe {
    public final Item product;
    public final Map<Item,Integer> ingredients;
    public final int amountPerBulk;
    public final long buildTimeMillis;
    public final float hickUpChance;


    public Recipe(Item product, Map<Item, Integer> ingredients, long buildTimeMillis, float hickUpChance) {
        this(product, 1, ingredients, buildTimeMillis, hickUpChance);
    }

    public Recipe(Item product, int amountPerBulk, Map<Item, Integer> ingredients, long buildTimeMillis, float hickUpChance) {
        this.amountPerBulk = amountPerBulk;
        this.product = product;
        this.ingredients = ingredients;
        this.buildTimeMillis = buildTimeMillis;
        this.hickUpChance = hickUpChance;
    }

    public int getIngredientAmount(Item item) {
        return ingredients.get(item);
    }
}
