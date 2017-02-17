package ch.chrummibei.silvercoin.universe.position;

import ch.chrummibei.silvercoin.universe.item.Item;

/**
 * An amount of items that can be held by a trader
 */
public class ItemPosition {
    final Item item;
    int amount;

    public ItemPosition(Item item, int amount) {
        this.item = item;
        this.amount = amount;
    }

    public Item getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    /**
     * Add another position to this one. Amounts will be added. Only possible if position are of the same item.
     * @param other Position to be added to this one.
     */
    public void add(ItemPosition other) {
        if (item != other.getItem()) {
            throw new IllegalArgumentException("Inventory items must be the same when adding");
        }

        amount += other.getAmount();
    }

    /**
     * Add to the position
     * @param amount The amount of items to add to the position.
     */
    public void addItems(int amount) {
        this.amount += amount;
    }


    public void removeItems(int amount) {
        this.addItems(-amount);
    }
}
