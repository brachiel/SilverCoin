package ch.chrummibei.silvercoin.universe.item;

import java.util.Map;

/**
 * Created by brachiel on 10/02/2017.
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
