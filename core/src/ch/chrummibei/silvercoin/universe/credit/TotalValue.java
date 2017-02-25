package ch.chrummibei.silvercoin.universe.credit;

/**
 * A TotalValue is the total price of positions. Can be converted to Price per unit.
 */
public class TotalValue extends Credit<TotalValue> {
    public TotalValue(double balance) {
        super(balance);
    }
    public TotalValue copy() { return new TotalValue(balance); }

    public TotalValue(Credit credit) {
        super(credit.balance);
    }

    public Price toPrice(int amount) {
        if (amount == 0) throw new RuntimeException("Amount was 0; cannot calculate price.");
        return new Price(balance / amount);
    }
}
