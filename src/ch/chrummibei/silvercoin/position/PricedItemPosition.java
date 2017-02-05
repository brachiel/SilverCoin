package ch.chrummibei.silvercoin.position;

import ch.chrummibei.silvercoin.item.Item;
import ch.chrummibei.silvercoin.credit.Price;
import ch.chrummibei.silvercoin.credit.TotalValue;

/**
 * An amount of items that can be held by a trader
 */
public class PricedItemPosition extends ItemPosition {
    TotalValue purchaseValue = new TotalValue(0);

    public PricedItemPosition(Item item) {
        this(item, 0, new TotalValue(0.0));
    }

    public PricedItemPosition(Item item, int amount) {
        this(item, amount, new TotalValue(0.0));
    }

    public PricedItemPosition(Item item, int amount, TotalValue purchaseValue) {
        super(item, amount);
        increasingPosition(amount, purchaseValue);
    }

    public PricedItemPosition(Item item, int amount, Price purchasePrice) {
        super(item, amount);
        increasingPosition(amount, purchasePrice.toTotalValue(amount));
    }


    public void add(PricedItemPosition other) {
        addItems(other.amount, other.getPurchaseValue());
    }

    /**
     * Add to the inventory, increasing the purchase value by unit.
     * @param amount The amount of items to add to the inventory.
     * @param pricePerUnit The price of one unit of item.
     */
    public void addItems(int amount, Price pricePerUnit) {
        addItems(amount, pricePerUnit.toTotalValue(amount));
    }

    /**
     * Add to the inventory, increasing the purchase value by the given value.
     * @param amount The amount of items to add to the inventory.
     * @param totalValue The total value of the added items.
     */
    public void addItems(int amount, TotalValue totalValue) {
        if (this.amount == 0) {
            increasingPosition(amount, totalValue);
        } else if (Math.signum(this.amount) == Math.signum(amount)) {
            increasingPosition(amount, totalValue);
        } else if (Math.abs(amount) < Math.abs(this.amount)) {
            decreasingPosition(amount, totalValue);
        } else if (this.amount == -amount) {
            zeroingPosition(amount, totalValue);
        } else {
            flippingPosition(amount, totalValue);
        }

        super.addItems(amount);
    }

    void flippingPosition(int amount, TotalValue totalValue) {
        int decreasingAmount = -this.amount;
        int increasingAmount = amount+this.amount;

        TotalValue decreasingValue = totalValue.toPrice(amount).toTotalValue(decreasingAmount);
        TotalValue increasingValue = totalValue.toPrice(amount).toTotalValue(increasingAmount);

        zeroingPosition(decreasingAmount, decreasingValue);
        increasingPosition(increasingAmount, increasingValue);
    }

    void zeroingPosition(int amount, TotalValue totalValue) {
        purchaseValue.set(0);
    }

    void decreasingPosition(int amount, TotalValue totalValue) {
        // Decrease has no effect on purchase Price
    }

    void increasingPosition(int amount, TotalValue totalValue) {
        purchaseValue.iadd(totalValue);
    }

    public void removeItems(int amount) {
        this.addItems(-amount, purchaseValue.toPrice(this.amount).toTotalValue(-amount));
    }

    /**
     * Remove the given amount of items worth the given price. This will influence the realised profit.
     * @param amount The amount to reduce the inventory by.
     * @param pricePerUnit The price of one unit of removed item.
     */
    public void removeItems(int amount, Price pricePerUnit) {
        addItems(-amount, pricePerUnit.toTotalValue(-amount));
    }

    /**
     * Remove the given amount of items worth the given price. This will influence the realised profit.
     * @param amount The amount to reduce the inventory by.
     * @param totalValue The positive total value of the removed items.
     */
    public void removeItems(int amount, TotalValue totalValue) {
        addItems(-amount, totalValue.invert());
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
