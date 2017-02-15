package ch.chrummibei.silvercoin.config;

import ch.chrummibei.silvercoin.universe.item.CraftableItem;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.item.Recipe;

import java.util.ArrayList;

/**
 * Created by brachiel on 15/02/2017.
 */
public interface UniverseConfig {
    ArrayList<Recipe> getRecipes();
    ArrayList<Item> getItems();
    ArrayList<CraftableItem> getCraftableItems();
}
