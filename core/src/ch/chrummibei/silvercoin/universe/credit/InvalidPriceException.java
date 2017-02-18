package ch.chrummibei.silvercoin.universe.credit;

/**
 * The price is invalid. Probably trying to divide a Total Value by an amount of 0.
 */
public class InvalidPriceException extends Throwable {
    public InvalidPriceException(String s) {
        super(s);
    }
}
