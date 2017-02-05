package ch.chrummibei.silvercoin.credit;

/**
 * A balance of currency. Used for type safety.
 */
public class Credit {
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

    public Credit add(double o) { return new Credit(balance + o); }
    public Credit add(Credit o) { return add(o.balance); }
    public Credit add(int o) { return new Credit(balance + o); }
    public void iadd(Credit o) { balance += o.balance; }

    public Credit subtract(double o) { return new Credit(balance - o); }
    public Credit subtract(Credit o) { return subtract(o.balance); }
    public Credit subtract(int o) { return new Credit(balance - o); }
    public void isubtract(Credit o) { balance -= o.balance; }

    public void set(double o) { balance = o; }
    public void set(Credit o) { set(o.balance); }
    public void set(int o) { balance = o; }

    public Credit invert() {
        return new Credit(-balance);
    }
    public void iinvert() {
        balance *= -1;
    }

}
