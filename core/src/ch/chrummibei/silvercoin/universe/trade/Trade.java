package ch.chrummibei.silvercoin.universe.trade;

import ch.chrummibei.silvercoin.universe.components.NamedComponent;
import ch.chrummibei.silvercoin.universe.credit.TotalValue;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.position.PricedItemPosition;
import com.badlogic.ashley.core.Entity;

import java.util.Optional;

/**
 * Trade containing amount and price of item to be traded.
 */
public class Trade {
    private final Item item;
    private final int amount;
    private final TotalValue totalValue;
    private Entity buyer = null;
    private Entity seller = null;
    public TradeOffer fromTradeOffer;

    public String toString() {
        return "Trade: " + amount + " " + item + ": " +
                Optional.ofNullable(Mappers.named.get(seller)).orElse(new NamedComponent("NoNameEntity")).name + " -> " +
                Optional.ofNullable(Mappers.named.get(buyer)).orElse(new NamedComponent("NoNameEntity")).name + " for " + totalValue;
    }

    public Trade(TradeOffer tradeOffer, Entity acceptingTrader, int acceptingAmount) {
        if (acceptingAmount == 0) throw new RuntimeException("Trading amount=0. This is a bug.");

        if (tradeOffer.getType() == TradeOffer.TYPE.BUYING) {
            this.seller = acceptingTrader;
            this.buyer = tradeOffer.getOfferingTrader();
        } else {
            this.buyer = acceptingTrader;
            this.seller = tradeOffer.getOfferingTrader();
        }

        this.item = tradeOffer.getItem();
        this.amount = acceptingAmount;
        this.totalValue = new TotalValue(acceptingAmount * tradeOffer.getPrice().toDouble());
    }

    public Item getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public Double getPrice() {
        return totalValue.toDouble() / amount;
    }
    public Double getTotalValue() {
        return totalValue.toDouble();
    }

    public Entity getBuyer() {
        return buyer;
    }

    public Entity getSeller() {
        return seller;
    }

    public PricedItemPosition getTradersItemPosition(Entity trader) throws TraderNotInvolvedException {
        if (amount == 0) {
            throw new RuntimeException("Trading item positions with amount = 0 doesn't make sense. This is a bug.");
        }

        if (trader == buyer) {
            return new PricedItemPosition(item, amount, totalValue);
        } else if (trader == seller) {
            return new PricedItemPosition(item, -amount, totalValue.invert());
        } else {
            throw new TraderNotInvolvedException();
        }
    }
}
