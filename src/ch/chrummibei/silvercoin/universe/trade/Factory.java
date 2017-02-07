package ch.chrummibei.silvercoin.universe.trade;

import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.item.CraftableItem;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.position.PricedItemPosition;
import ch.chrummibei.silvercoin.universe.position.YieldingItemPosition;

import java.util.Map;

/**
 * A Factory is a trader who is able to convert Items to a specific CraftableItem.
 * Each Factory can only produce a single CraftableItem.
 */
public class Factory extends Trader {
    private int goalStock;

    private CraftableItem product;
    private Map<Item, PricedItemPosition> inventory;
    private YieldingItemPosition productStock;

    public Factory(CraftableItem product, int goalStock) {
        this.product = product;
        this.goalStock = goalStock;
    }

    public int calcProducibleAmount(Item item, int ownedAmount) {
        return ownedAmount / product.getIngredientAmount(item);
    }

    public int calcProducibleAmount() {
        return inventory.values().stream()
                .mapToInt(pos -> calcProducibleAmount(pos.getItem(), pos.getAmount()))
                .min()
                .orElse(0);
    }

    public void produceProduct() {
        int producingAmount = calcProducibleAmount();
        Price productPrice = new Price(0);
        for (PricedItemPosition position : inventory.values()) {
            int ingredientAmount = product.getIngredientAmount(position.getItem());
            // Add the ingredient price to the product price
            productPrice = productPrice.add(position.getPurchasePrice().toTotalValue(ingredientAmount));
            // Reduce inventory by amount needed to produce the product
            position.removeItems(producingAmount * ingredientAmount, position.getPurchasePrice());
        }

        // Add the produced products
        productStock.addItems(producingAmount, productPrice);
    }
}
