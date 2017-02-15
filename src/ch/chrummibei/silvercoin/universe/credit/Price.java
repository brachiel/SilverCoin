package ch.chrummibei.silvercoin.universe.credit;

/**
 * A price is a Credit per Unit of an Item. Can be converted to TotalValue. Used for type safety.
 */
public class Price extends Credit<Price> {
    public Price(double balance) {
        super(balance);
    }

    public Price(Credit credit) {
        super(credit.balance);
    }

    public TotalValue toTotalValue(int amount) {
        return new TotalValue(balance * amount);
    }
}
