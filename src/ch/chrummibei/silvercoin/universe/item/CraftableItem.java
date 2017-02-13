package ch.chrummibei.silvercoin.universe.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An item that can be crafted from other items in factories.
 */
public class CraftableItem extends Item {
    private final ArrayList<Recipe> recipes = new ArrayList<>();

    public CraftableItem(String name) {
        super(name);
    }

    public void addRecipe(Recipe recipe) {
        recipes.add(recipe);
    }

    public ArrayList<Recipe> getRecipes() {
        return recipes;
    }
}
