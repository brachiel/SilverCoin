package ch.chrummibei.silvercoin.item;

import ch.chrummibei.silvercoin.item.Item;

import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * An item that can be crafted from other items in factories.
 */
public class CraftableItem extends Item {
    private Map<Item, Integer> ingredients;

    public CraftableItem(String name) {
        super(name);
        ingredients = new HashMap<>();
    }

    public void addIngredient(Item ingredient, Integer amount) {
        ingredients.put(ingredient, amount);
    }

    public Integer getIngredientAmount(Item dependency) {
        return ingredients.get(dependency);
    }

    public String getIngredientString() {
        return toString() + " <- " + ingredients.keySet().stream()
                                        .map(Item::toString)
                                        .collect(Collectors.joining(", "));
    }
}
