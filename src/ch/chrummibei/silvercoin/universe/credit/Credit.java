package ch.chrummibei.silvercoin.universe.credit;

/**
 * A balance of currency. Used for type safety.
 */
public class Credit<T extends Credit<T>> {
    double balance;

    public Credit() {
    }

    public Credit(double balance) {
        set(balance);
    }

    public T copy() {
        return new Credit<T>().set(balance);
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

    public T add(double o) { return this.copy().iAdd(o); }
    public T add(Credit o) { return add(o.balance); }
    public T iAdd(double o) { balance += o; return (T) this; }
    public T iAdd(Credit o) { balance += o.balance; return (T) this; }

    public T subtract(double o) { return this.copy().iSubtract(o); }
    public T subtract(Credit o) { return subtract(o.balance); }
    public T iSubtract(double o) { balance -= o; return (T) this; }
    public T iSubtract(Credit o) { balance -= o.balance; return (T) this; }

    public T set(double o) { balance = o; return (T) this; }
    public T set(Credit o) { set(o.balance); return (T) this; }
    public T set(int o) { balance = o; return (T) this; }

    public T invert() { return this.copy().iInvert(); }
    public T iInvert() { balance *= -1; return (T) this; }

    public T multiply(double factor) { return this.copy().iMultiply(factor); }
    public T iMultiply(double factor) { balance *= factor; return (T) this; }
    public T divide(double factor) { return multiply(1/factor); }
    public T iDivide(double factor) { iMultiply(1/factor); return (T) this; }

}
