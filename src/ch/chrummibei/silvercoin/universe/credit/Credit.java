package ch.chrummibei.silvercoin.universe.credit;

/**
 * A balance of currency. Used for type safety.
 */
public class Credit<CHILD extends Credit<CHILD>> {
    double balance;
    public Credit(double balance) {
        this.balance = balance;
    }

    public long toLong() {
        return Math.round(balance);
    }

    public double toDouble() {
        return balance;
    }

    public String toString() {
        return String.format("%.02f Cr.", balance);
    }

    public CHILD add(double o) { return (CHILD) new Credit(balance + o); }
    public CHILD add(Credit o) { return add(o.balance); }
    public CHILD add(int o) { return add(balance + o); }
    public void iAdd(Credit o) { balance += o.balance; }

    public CHILD subtract(double o) { return (CHILD) new Credit(balance - o); }
    public CHILD subtract(Credit o) { return subtract(o.balance); }
    public CHILD subtract(int o) { return subtract(balance - o); }
    public void iSubtract(Credit o) { balance -= o.balance; }

    public void set(double o) { balance = o; }
    public void set(Credit o) { set(o.balance); }
    public void set(int o) { balance = o; }

    public CHILD invert() {
        return (CHILD) new Credit(-balance);
    }
    public void iInvert() {
        balance *= -1;
    }

    public void iMultiply(double factor) { balance *= factor; }
    public void iDivide(double factor) { iMultiply(1/factor); }

    public CHILD multiply(double factor) {
        return (CHILD) new Credit(balance*factor);
    }
}
