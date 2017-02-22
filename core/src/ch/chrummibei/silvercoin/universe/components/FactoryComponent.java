package ch.chrummibei.silvercoin.universe.components;

import ch.chrummibei.silvercoin.universe.item.Recipe;
import com.badlogic.ashley.core.Component;

/**
 * Created by brachiel on 20/02/2017.
 */
public class FactoryComponent implements Component {
    public int goalStock;
    public Recipe recipe;
    public double priceSpreadFactor = 1.2;
    public long timeReservoirMillis = 0;

    public FactoryComponent(Recipe recipe, int goalStock, double spreadFactor) {
        this.recipe = recipe;
        this.goalStock = goalStock;
        this.priceSpreadFactor = spreadFactor;
    }
}
