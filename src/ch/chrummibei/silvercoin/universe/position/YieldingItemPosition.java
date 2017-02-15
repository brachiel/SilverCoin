package ch.chrummibei.silvercoin.universe.position;

import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.credit.TotalValue;
import ch.chrummibei.silvercoin.universe.item.Item;

/**
 * A PricedItemPosition that also keeps track of the realised profit.
 */
public class YieldingItemPosition extends PricedItemPosition {
    private TotalValue realisedProfit = new TotalValue(0);

    public YieldingItemPosition(Item item) {
        super(item);
    }

    public YieldingItemPosition(Item item, int amount) {
        super(item, amount);
    }

    public YieldingItemPosition(Item item, int amount, TotalValue purchaseValue) {
        super(item, amount, purchaseValue);
    }

    public YieldingItemPosition(Item item, int amount, Price purchasePrice) {
        super(item, amount, purchasePrice);
    }

    public YieldingItemPosition(PricedItemPosition position) {
        this(position.getItem(), position.getAmount(), position.getPurchaseValue());
    }

    @Override
    public void add(PricedItemPosition other) {
        super.add(other);
    }

    @Override
    public void addItems(int amount, Price pricePerUnit) {
        super.addItems(amount, pricePerUnit);
    }

    @Override
    public void removeItems(int amount) {
        super.removeItems(amount);
    }

    @Override
    public void removeItems(int amount, Price pricePerUnit) {
        super.removeItems(amount, pricePerUnit);
    }

    @Override
    public void removeItems(int amount, TotalValue totalValue) {
        super.removeItems(amount, totalValue);
    }

    @Override
    public void addItems(int amount, TotalValue totalValue) {
        super.addItems(amount, totalValue);
    }

    @Override
    void flippingPosition(int amount, TotalValue totalValue) {
        super.flippingPosition(amount, totalValue);
    }

    // PricedItemPosition gives us callbacks for the interesting operations:
    @Override
    void zeroingPosition(int amount, TotalValue totalValue) {
        decreasingPosition(amount, totalValue);
        super.zeroingPosition(amount, totalValue);
    }

    @Override
    void decreasingPosition(int amount, TotalValue totalValue) {
        Price sellingPrice = totalValue.toPrice(amount);
        Price priceDifference = getPurchasePrice().subtract(sellingPrice);
        TotalValue profit = priceDifference.toTotalValue(amount);
        realisedProfit.iAdd(profit);
        super.decreasingPosition(amount, totalValue);
    }

    public TotalValue getRealisedProfit() {
        return realisedProfit;
    }
}
