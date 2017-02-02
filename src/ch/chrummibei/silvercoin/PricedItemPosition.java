package ch.chrummibei.silvercoin;

/**
 * An amount of items that can be held by a trader
 */
public class PricedItemPosition extends ItemPosition {
    private TotalValue purchaseValue;

    public PricedItemPosition(Item item) {
        this(item, 0, new TotalValue(0.0));
    }

    public PricedItemPosition(Item item, int amount) {
        this(item, amount, new TotalValue(0.0));
    }

    public PricedItemPosition(Item item, int amount, TotalValue purchaseValue) {
        super(item, amount);
        this.purchaseValue = purchaseValue;
    }

    public PricedItemPosition(Item item, int amount, Price purchasePrice) {
        super(item, amount);
        this.purchaseValue = purchasePrice.toTotalValue(amount);
    }


    public void add(PricedItemPosition other) {
        super.add(other);
        purchaseValue.add(other.getPurchaseValue());
    }

    /**
     * Add to the inventory, increasing the purchase value by unit.
     * @param amount The amount of items to add to the inventory.
     * @param pricePerUnit The price of one unit of item.
     */
    public void addItems(int amount, Price pricePerUnit) {
        super.addItems(amount);
        purchaseValue.add(pricePerUnit.toTotalValue(amount));
    }

    /**
     * Add to the inventory, increasing the purchase value by the given value.
     * @param amount The amount of items to add to the inventory.
     * @param totalValue The total value of the added items.
     */
    public void addItems(int amount, TotalValue totalValue) {
        super.addItems(amount);
        purchaseValue.add(totalValue);
    }

    public void removeItems(int amount) {
        this.removeItems(amount, purchaseValue.toPrice(this.getAmount()).toTotalValue(amount));
    }

    /**
     * Remove the given amount of items worth the given price. This will influence the realised profit.
     * @param amount The amount to reduce the inventory by.
     * @param pricePerUnit The price of one unit of removed item.
     */
    public void removeItems(int amount, Price pricePerUnit) {
        removeItems(amount, pricePerUnit.toTotalValue(amount));
    }

    /**
     * Remove the given amount of items worth the given price. This will influence the realised profit.
     * @param amount The amount to reduce the inventory by.
     * @param totalValue The positive total value of the removed items.
     */
    public void removeItems(int amount, TotalValue totalValue) {
        purchaseValue.subtract(totalValue);
        super.removeItems(amount);
    }

    /**
     * The purchase price of one item.
     * @return Price per one item.
     */
    public Price getPurchasePrice() {
        return purchaseValue.toPrice(getAmount());
    }

    /**
     * The purchase value of the whole amount of this item currently in stock.
     * @return TotalValue of all owned items.
     */
    public TotalValue getPurchaseValue() {
        return purchaseValue;
    }

}
