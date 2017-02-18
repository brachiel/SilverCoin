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

    /* Debug NaN
    private void check(Object o) { if (o != o) throw new RuntimeException("Object is invalid"); }
    private void check(int o) { if (o != o) throw new RuntimeException("Object is invalid"); }
    private void check(double o) { if (o != o) throw new RuntimeException("Object is invalid"); }
    */
    
    public T add(double o) { /* check(o); */ return this.copy().iAdd(o); }
    public T add(Credit o) { /* check(o); */ return add(o.balance); }
    @SuppressWarnings("unchecked")
    public T iAdd(double o) { /* check(o); */ balance += o; return (T) this; }
    @SuppressWarnings("unchecked")
    public T iAdd(Credit o) { /* check(o); */ balance += o.balance; return (T) this; }

    public T subtract(double o) { /* check(o); */ return this.copy().iSubtract(o); }
    public T subtract(Credit o) { /* check(o); */ return subtract(o.balance); }
    @SuppressWarnings("unchecked")
    public T iSubtract(double o) { /* check(o); */ balance -= o; return (T) this; }
    @SuppressWarnings("unchecked")
    public T iSubtract(Credit o) { /* check(o); */ balance -= o.balance; return (T) this; }

    @SuppressWarnings("unchecked")
    public T set(double o) { /* check(o); */ balance = o; return (T) this; }
    @SuppressWarnings("unchecked")
    public T set(Credit o) { /* check(o); */ set(o.balance); return (T) this; }
    @SuppressWarnings("unchecked")
    public T set(int o) { /* check(o); */ balance = o; return (T) this; }

    public T invert() { return this.copy().iInvert(); }
    @SuppressWarnings("unchecked")
    public T iInvert() { balance *= -1; return (T) this; }

    public T multiply(double factor) { /* check(factor); */ if (factor != factor) throw new RuntimeException("Factor is NaN"); return this.copy().iMultiply(factor); }
    @SuppressWarnings("unchecked")
    public T iMultiply(double factor) { /* check(factor); */ if (factor != factor) throw new RuntimeException("Factor is NaN"); balance *= factor; return (T) this; }

    public T divide(double factor) { /* check(factor); */ if (factor == 0) throw new RuntimeException("Trying to divide by 0"); return multiply(1/factor); }
    @SuppressWarnings("unchecked")
    public T iDivide(double factor) { /* check(factor); */ iMultiply(1/factor); return (T) this; }
}
