package ch.chrummibei.silvercoin.universe.trade;

import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.item.Item;
import com.badlogic.ashley.core.Entity;

import java.util.Optional;

/**
 * Created by brachiel on 24/02/2017.
 */
public class TradeNeed {
    public Item item;
    public TradeOffer.TYPE type;
    public Optional<Integer> maxAmount;
    public Optional<Price> priceLimit;

    public TradeNeed(Item item, TradeOffer.TYPE type) {
        this(item, type, null, null);
    }

    public TradeNeed(Item item, TradeOffer.TYPE type, Integer maxAmount, Price price) {
        this.item = item;
        this.type = type;
        this.maxAmount = Optional.ofNullable(maxAmount);
        this.priceLimit = Optional.ofNullable(price);
    }

    public TradeOffer toTradeOffer(Entity offeringTrader) {
        if (! maxAmount.isPresent()) return null;
        if (! priceLimit.isPresent()) return null;
        return new TradeOffer(offeringTrader, item, type, maxAmount.get(), priceLimit.get());
    }

    public void unify(TradeNeed need) {
        if (priceLimit.isPresent() && need.priceLimit.isPresent()
                && maxAmount.isPresent() && need.maxAmount.isPresent()) {
            priceLimit = Optional.of(
                    priceLimit.get().toTotalValue(maxAmount.get())
                        .add(need.maxAmount.get())
                                .toPriceNotNull(maxAmount.get() + need.maxAmount.get())
            );
        } else {
            if (need.priceLimit.isPresent()) {
                priceLimit = need.priceLimit;
            }
        }

        if (maxAmount.isPresent() && need.maxAmount.isPresent()) {
            maxAmount = Optional.of(maxAmount.get() + need.maxAmount.get());
        } else {
            if (need.maxAmount.isPresent()) {
                maxAmount = need.maxAmount;
            }
        }
    }
}
