package ch.chrummibei.silvercoin.config;

import ch.chrummibei.silvercoin.universe.item.CraftableItem;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.item.Recipe;

import java.util.ArrayList;

/**
 * Item Config interface that an item mod needs to deliver.
 */
public interface ItemConfig {
    ArrayList<Recipe> getRecipes();
    ArrayList<Item> getItems();
    ArrayList<CraftableItem> getCraftableItems();
}
