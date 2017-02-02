package ch.chrummibei.silvercoin;

/**
 * An amount of items that can be held by a trader
 */
public class InventoryItem {
    Item item;
    int amount;

    public InventoryItem(Item item, int amount) {
        this.item = item;
        this.amount = amount;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
