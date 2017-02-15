package ch.chrummibei.silvercoin.universe.credit;

/**
 * A TotalValue is the total price of items. Can be converted to Price per unit.
 */
public class TotalValue extends Credit<TotalValue> {
    public TotalValue(double balance) {
        super(balance);
    }

    public TotalValue(Credit credit) {
        super(credit.balance);
    }

    public Price toPrice(int amount) {
        return new Price(balance / amount);
    }
}
