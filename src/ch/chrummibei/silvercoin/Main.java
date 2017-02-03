package ch.chrummibei.silvercoin;

import ch.chrummibei.silvercoin.actor.ArbitrageTradeActor;
import ch.chrummibei.silvercoin.item.CraftableItem;
import ch.chrummibei.silvercoin.item.Item;
import ch.chrummibei.silvercoin.credit.Price;
import ch.chrummibei.silvercoin.space.Universe;
import ch.chrummibei.silvercoin.trade.ArbitrageTrader;
import ch.chrummibei.silvercoin.trade.Market;
import ch.chrummibei.silvercoin.trade.TradeOffer;
import ch.chrummibei.silvercoin.trade.Trader;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Universe universe = new Universe();
        universe.run();
    }
}


