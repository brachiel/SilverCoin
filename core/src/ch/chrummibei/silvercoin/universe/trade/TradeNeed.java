package ch.chrummibei.silvercoin.universe.trade;

import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.item.Item;

import java.util.Optional;

/**
 * Created by brachiel on 24/02/2017.
 */
public class TradeNeed {
    public Item item;
    public int amount;
    public Optional<Price> priceLimit;

    public TradeNeed(Item item, int amount) {
        this(item, amount, null);
    }

    public TradeNeed(Item item, int amount, Price price) {
        this.item = item;
        this.amount = amount;
        this.priceLimit = Optional.ofNullable(price);

        if (amount == 0) throw new RuntimeException("Need with amount = 0 doesn't make sense");
    }

    public String toString() {
        return "Need " + amount + " of " + item;
    }

    public void unify(TradeNeed need) {
        if (priceLimit.isPresent() && need.priceLimit.isPresent()) {
            priceLimit = Optional.of(
                    priceLimit.get().toTotalValue(amount)
                        .add(need.amount)
                                .toPriceNotNull(amount + need.amount)
            );
        } else {
            if (need.priceLimit.isPresent()) {
                priceLimit = need.priceLimit;
            }
        }

        amount += need.amount;
    }

    public TradeOffer.TYPE type() {
        return TradeOffer.TYPE.fromAmount(amount);
    }
}
