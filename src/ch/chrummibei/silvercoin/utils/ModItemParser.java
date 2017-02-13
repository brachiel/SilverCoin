package ch.chrummibei.silvercoin.utils;

import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.item.Recipe;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by brachiel on 10/02/2017.
 */
public class ModItemParser {
    ArrayList<Item> items = new ArrayList<>();
    ArrayList<Recipe> recipes = new ArrayList<>();

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
        HashMap<String,ArrayList<HashMap<String,Integer>>> rawRecipies = new HashMap<>();

        JSONArray jsonItems = (JSONArray) modItemConfig.get("items");
        for (Iterator it = jsonItems.iterator(); it.hasNext(); ) {
            String itemName = (String) it.next();
            Item item = new Item(itemName);
        }

        JSONArray jsonRecipes = (JSONArray) modItemConfig.get("recipes");
        for (recipe : jsonRecipes.iterator()) {
            JSONArray 
        }
    }
}
