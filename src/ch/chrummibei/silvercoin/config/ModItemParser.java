package ch.chrummibei.silvercoin.config;

import ch.chrummibei.silvercoin.universe.item.CraftableItem;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.item.Recipe;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses the item configuration file.
 */
public class ModItemParser implements ItemConfig {
    private final ArrayList<Recipe> recipes = new ArrayList<>();
    private final ArrayList<Item> items = new ArrayList<>();
    private final ArrayList<CraftableItem> craftableItems = new ArrayList<>();

    public ArrayList<Recipe> getRecipes() {
        return recipes;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public ArrayList<CraftableItem> getCraftableItems() {
        return craftableItems;
    }

    @SuppressWarnings("unchecked")
    public ModItemParser(Reader itemConfigReader) {
        JSONObject modItemConfig = JSONConfigParser.configParse(itemConfigReader);

        ArrayList<String> itemNames = new ArrayList<>();
        ((JSONArray) modItemConfig.get("items")).forEach(itemName ->
                itemNames.add((String) itemName)
        );

        HashMap<String, Item> itemHash = new HashMap<>();

        // Walk through all recipes and create all items that we have recipes for.
        ((JSONArray) modItemConfig.get("recipes")).forEach(rawRecipe -> {
            JSONObject recipe = (JSONObject) rawRecipe;
            String productName = (String) recipe.get("product");
            CraftableItem item = new CraftableItem(productName);
            itemHash.put(productName, item);
            this.craftableItems.add(item);
        });

        // Create all Items that are missing
        itemNames.stream().filter(itemName -> ! itemHash.containsKey(itemName)).forEach(itemName -> {
            Item item = new Item(itemName);
            itemHash.put(itemName, item);
            this.items.add(item);
        });


        ((JSONArray) modItemConfig.get("recipes")).forEach(rawRecipe -> {
            JSONObject recipe = (JSONObject) rawRecipe;
            String productName = (String) recipe.get("product");
            if (! itemNames.contains(productName)) {
                System.out.println("Recipe found for non-existent item: " + productName);
            }

            HashMap<Item, Integer> ingredients = new HashMap<>();
            try {
                Map rawIngredients = (Map) recipe.get("ingredients");

                rawIngredients.forEach((itemName, amount) ->
                        ingredients.put(itemHash.get(itemName), ((Long) amount).intValue())
                );
            } catch (NullPointerException e) {
                // No ingredients for this recipe :). It's free.
            }

            long buildTime = (long) recipe.get("buildTime");

            CraftableItem product = (CraftableItem) itemHash.get(productName);
            Recipe newRecipe = new Recipe(product, ingredients, buildTime);
            product.addRecipe(newRecipe);
            this.recipes.add(newRecipe);
        });
    }
}
