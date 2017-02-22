package ch.chrummibei.silvercoin.universe.components;

import ch.chrummibei.silvercoin.universe.item.Item;
import com.badlogic.ashley.core.Component;

/**
 * Created by brachiel on 21/02/2017.
 */
public class BigSpenderComponent implements Component {
    public final Item itemToConsume;
    public final float consumptionTime;
    public float timeReservoir = 0;

    public BigSpenderComponent(Item itemToConsume, float consumptionTime) {
        this.itemToConsume = itemToConsume;
        this.consumptionTime = consumptionTime;
    }
}
