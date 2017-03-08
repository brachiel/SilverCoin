package ch.chrummibei.silvercoin.universe.entity_factories;

import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.components.InventoryComponent;
import ch.chrummibei.silvercoin.universe.components.NamedComponent;
import ch.chrummibei.silvercoin.universe.components.TraderComponent;
import ch.chrummibei.silvercoin.universe.components.WalletComponent;
import ch.chrummibei.silvercoin.universe.credit.Credit;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.entity_systems.TraderSystem;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.position.PricedItemPosition;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;

/**
 * Created by brachiel on 21/02/2017.
 */
public class TraderEntityFactory {
    public static int traderSequence = 0;

    public static Entity RandomisedTraderEntity(ArrayList<Item> catalogue) {
        Entity entity = new Entity();
        Credit credit = new Credit(0);
        InventoryComponent inventory = new InventoryComponent();
        TraderComponent trader = new TraderComponent();

        entity.add(new WalletComponent(credit));
        entity.add(trader);
        entity.add(inventory);
        entity.add(new NamedComponent("Trader " + traderSequence++));

        for (int j = 0, maxJ = Universe.getRandomInt(1,3); j < maxJ; ++j) {
            Item item = catalogue.get(Universe.getRandomInt(0, catalogue.size()-1));
            TradeOffer offer = new TradeOffer(entity, item, TradeOffer.TYPE.SELLING,
                    Universe.getRandomInt(1,10),
                    new Price(Universe.getRandomDouble(10,90)));
            TraderSystem.addPricedPositionToInventory(entity,
                    new PricedItemPosition(offer.getItem(), offer.getAmount(), offer.getTotalValue()));
            trader.tradeOffers.add(offer);
        }

        for (int j = 0, maxJ = Universe.getRandomInt(1,3); j < maxJ; ++j) {
            Item item = catalogue.get(Universe.getRandomInt(0, catalogue.size()-1));
            TradeOffer offer = new TradeOffer(entity, item, TradeOffer.TYPE.BUYING,
                    Universe.getRandomInt(1,10),
                    new Price(Universe.getRandomDouble(10,90)));
            credit.iAdd(offer.getTotalValue());
            trader.tradeOffers.add(offer);
        }

        return entity;
    }
}
