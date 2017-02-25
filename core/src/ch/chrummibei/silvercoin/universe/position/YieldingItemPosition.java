package ch.chrummibei.silvercoin.universe.position;

import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.credit.TotalValue;
import ch.chrummibei.silvercoin.universe.item.Item;

/**
 * A PricedItemPosition that also keeps track of the realised profit.
 */
public class YieldingItemPosition extends PricedItemPosition {
    private final TotalValue realisedProfit = new TotalValue(0);

    public String toString() {
        return this.getAmount() + " valued " + this.getPurchaseValue() + " profit: " + this.getRealisedProfit();
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

    public YieldingItemPosition(Item product) {
        super(product, 0, new TotalValue(0));
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
