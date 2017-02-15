package ch.chrummibei.silvercoin.utils;

import ch.chrummibei.silvercoin.universe.item.CraftableItem;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.item.Recipe;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by brachiel on 10/02/2017.
 */
public class ModItemParser {
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

    public ModItemParser(String modItemFilePath) throws FileNotFoundException {
        FileReader fileReader = new FileReader(modItemFilePath);
        JSONParser parser = new JSONParser();
        JSONObject modItemConfig;

        try {
            modItemConfig = (JSONObject) parser.parse(fileReader);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        ArrayList<String> itemNames = new ArrayList<>();

        // Read raw images

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
            craftableItems.add(item);
        });

        // Create all Items that are missing
        itemNames.stream().filter(itemName -> ! itemHash.containsKey(itemName)).forEach(itemName ->
                itemHash.put(itemName, new Item(itemName))
        );


        ((JSONArray) modItemConfig.get("recipes")).forEach(rawRecipe -> {
            JSONObject recipe = (JSONObject) rawRecipe;
            String productName = (String) recipe.get("product");
            if (! itemNames.contains(productName)) {
                System.out.println("Recipie found for non-existant item: " + productName);
            }

            HashMap<Item, Integer> ingredients = new HashMap<>();

            Map rawIngredients = (Map) recipe.get("ingredients");

            rawIngredients.forEach((item, amount) ->
                    ingredients.put((Item) item, (Integer) amount)
            );

            CraftableItem product = (CraftableItem) itemHash.get(productName);
            Recipe newRecipe = new Recipe(product, ingredients);
            recipes.add(newRecipe);
            product.addRecipe(newRecipe);
        });
    }
}
