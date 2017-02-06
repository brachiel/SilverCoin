package ch.chrummibei.silvercoin.universe.credit;

/**
 * A price is a Credit per Unit of an Item. Can be converted to TotalValue. Used for type safety.
 */
public class Price extends Credit {
    public Price(double balance) {
        super(balance);
    }

    public Price(Credit credit) {
        super(credit.balance);
    }

    public TotalValue toTotalValue(int amount) {
        return new TotalValue(balance * amount);
    }

    public Price add(Price o) { return new Price(balance + o.balance); }
    @Override public Price add(Credit o) { return new Price(this.balance + o.balance); }
    @Override public Price add(double o) { return new Price(this.balance + o); }
    @Override public Price add(int o) { return new Price(this.balance + o); }
    public Price subtract(Price o) { return new Price(balance - o.balance); }
    @Override public Price subtract(Credit o) { return new Price(balance - o.balance); }
    @Override public Price subtract(double o) { return new Price(balance - o); }
    @Override public Price subtract(int o) { return new Price(balance - o); }
}
