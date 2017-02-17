package ch.chrummibei.silvercoin.universe.trade;

/**
 * A trade offer was accepted with more amount than was available. This is most likely because someone else
 * already took the trade offer in another Thread.
 */
public class TradeOfferHasNotEnoughAmountLeft extends Exception {
}
