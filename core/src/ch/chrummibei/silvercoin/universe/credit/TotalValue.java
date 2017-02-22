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

    public Price toPrice(int amount) throws InvalidPriceException {
        if (amount == 0) throw new InvalidPriceException("Amount was 0; cannot calculate price.");
        return new Price(balance / amount);
    }

    /**
     * Calculate price from total value if amount is not 0. This method will not throw an InvalidPriceException but
     * will throw a RuntimeException instead, if amount is 0. Call this only if you guarantee that amount != 0.
     * @param amount not 0
     * @return Calculated price from total value
     */
    public Price toPriceNotNull(int amount) {
        if (amount == 0) throw new RuntimeException("toPriceNotNull was called with amount=0. This is a bug.");
        return new Price(balance / amount);
    }
}
