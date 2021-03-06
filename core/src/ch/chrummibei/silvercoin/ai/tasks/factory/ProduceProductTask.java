package ch.chrummibei.silvercoin.ai.tasks.factory;

import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.components.FactoryComponent;
import ch.chrummibei.silvercoin.universe.components.InventoryComponent;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.entity_systems.FactorySystem;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.position.YieldingItemPosition;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

/**
 * Created by brachiel on 23/02/2017.
 */
public class ProduceProductTask extends LeafTask<Entity> {


    @Override
    public Status execute() {
        FactoryComponent factory = Mappers.factory.get(this.getObject());
        InventoryComponent inventory = Mappers.inventory.get(this.getObject());

        int producingBulks = FactorySystem.calcProducibleBulksWithIngredients(this.getObject());
        // Do not produce more than needed
        producingBulks = Math.min(producingBulks,
                factory.goalStock - FactorySystem.getProductPosition(this.getObject()).getAmount());

        if (producingBulks == 0) {
            // We don't have enough ingredients, so we can't start to produce. Resetting time reservoir.
            factory.timeReservoirMillis = Math.min(0, factory.timeReservoirMillis);
            return Status.FAILED;
        }

        // Calculate how many bulks we can produce with the time reservoir
        producingBulks = Math.min(producingBulks,
                Math.toIntExact(factory.timeReservoirMillis / factory.recipe.buildTimeMillis));

        if (producingBulks <= 0) {
            return Status.FAILED; // Not enough time has passed to produce.
        }

        factory.timeReservoirMillis -= producingBulks * factory.recipe.buildTimeMillis; // Subtract the time we needed from reservoir

        // Check if there was a hick up. If so, we need to repair the factors. This needs some time.
        if (Universe.getRandomDouble(0, 1) > factory.recipe.hickUpChance) {
            factory.timeReservoirMillis -= factory.recipe.buildTimeMillis * Universe.getRandomDouble(1, 3);
        }

        // Calculate product price before we start to remove items or else the price may become invalid.
        Price productPrice = FactorySystem.calcProductPriceFromPurchasePrice(this.getObject());
        for (Item ingredient : factory.recipe.ingredients.keySet()) {
            int ingredientAmount = factory.recipe.getIngredientAmount(ingredient);
            YieldingItemPosition position = inventory.positions.get(ingredient);

            position.removeItems(producingBulks * ingredientAmount, position.getPurchasePrice());
        }

        // Add the produced products
        FactorySystem.getProductPosition(this.getObject())
                .addItems(producingBulks * factory.recipe.amountPerBulk, productPrice);

        return Status.SUCCEEDED;
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        return task;
    }
}
