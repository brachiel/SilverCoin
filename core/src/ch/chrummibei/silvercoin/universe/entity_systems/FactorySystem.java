package ch.chrummibei.silvercoin.universe.entity_systems;

import ch.chrummibei.silvercoin.universe.components.FactoryComponent;
import ch.chrummibei.silvercoin.universe.components.InventoryComponent;
import ch.chrummibei.silvercoin.universe.components.MarketSightComponent;
import ch.chrummibei.silvercoin.universe.components.TraderComponent;
import ch.chrummibei.silvercoin.universe.credit.InvalidPriceException;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.position.YieldingItemPosition;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

/**
 * Created by brachiel on 20/02/2017.
 */
public class FactorySystem extends IteratingSystem {
    private static Family family = Family.all(FactoryComponent.class,
                                              MarketSightComponent.class,
                                              InventoryComponent.class,
                                              TraderComponent.class).get();
    public FactorySystem() {
        super(family);
    }

    public FactorySystem(int priority) {
        super(family, priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Mappers.factory.get(entity).timeReservoirMillis += deltaTime * 1000;

        // Logic is implemented by AIComponent
    }

    public static Price calcProductPriceFromPurchasePrice(Entity entity) {
        FactoryComponent factory = Mappers.factory.get(entity);
        InventoryComponent inventory = Mappers.inventory.get(entity);

        Price productPrice = new Price(factory.recipe.buildTimeMillis / 10); // Minimum price depends on buildTime
        factory.recipe.ingredients.forEach((ingredient, ingredientAmount) -> {
            YieldingItemPosition position = inventory.positions.get(ingredient);
            if (position.getAmount() == 0) {
                return;
            }

            try {
                productPrice.iAdd(position.getPurchasePrice().toTotalValue(ingredientAmount));
            } catch (InvalidPriceException e) {
                e.printStackTrace();
            }
        });

        return productPrice;
    }

    public static YieldingItemPosition getProductPosition(Entity entity) {
        FactoryComponent factory = Mappers.factory.get(entity);
        InventoryComponent inventory = Mappers.inventory.get(entity);

        return inventory.positions.get(factory.recipe.product);
    }

    public static int calcProducibleAmountWithIngredients(Entity entity) {
        FactoryComponent factory =  Mappers.factory.get(entity);
        InventoryComponent inventory = Mappers.inventory.get(entity);

        if (factory.recipe.ingredients.size() == 0) { // This recipe needs no ingredients. Production only depends on time
            return Integer.MAX_VALUE;
        }

        return factory.recipe.ingredients.keySet().stream()
                .mapToInt(item -> FactorySystem.calcProducibleAmount(entity, item))
                .min()
                .orElse(0);
    }

    public static int calcProducibleAmount(Entity entity, Item item) {
        FactoryComponent factory =  Mappers.factory.get(entity);
        InventoryComponent inventory = Mappers.inventory.get(entity);

        return inventory.positions.get(item).getAmount() / factory.recipe.ingredients.get(item);
    }

    public static int calcNeededIngredientAmount(Entity entity, Item item) {
        FactoryComponent factory = Mappers.factory.get(entity);
        return factory.recipe.ingredients.get(item) * factory.goalStock;
    }
}
