package ch.chrummibei.silvercoin.universe.trade;

/**
 * A trader was told to execute a trade; but the trader is no involved with this trade. If this happens, is's a bug.
 */
public class TraderNotInvolvedException extends Exception {
}
