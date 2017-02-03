package ch.chrummibei.silvercoin.credit;

/**
 * A TotalValue is the total price of items. Can be converted to Price per unit.
 */
public class TotalValue extends Credit {
    public TotalValue(double balance) {
        super(balance);
    }

    public TotalValue(Credit credit) {
        super(credit.balance);
    }

    public Price toPrice(int amount) {
        return new Price(balance / amount);
    }

    public TotalValue add(TotalValue o) { return new TotalValue(balance + o.balance); }
    @Override public TotalValue add(Credit o) { return new TotalValue(balance + o.balance); }
    @Override public TotalValue add(double o) { return new TotalValue(balance + o); }
    @Override public TotalValue add(int o) { return new TotalValue(balance + o); }
    public TotalValue subtract(TotalValue o) { return new TotalValue(balance - o.balance); }
    @Override public TotalValue subtract(Credit o) { return new TotalValue(balance - o.balance); }
    @Override public TotalValue subtract(double o) { return new TotalValue(balance - o); }
    @Override public TotalValue subtract(int o) { return new TotalValue(balance - o); }

    @Override public TotalValue invert() {
        return new TotalValue(-balance);
    }
}
