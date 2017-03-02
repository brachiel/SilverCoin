package ch.chrummibei.silvercoin.universe.entity_factories;

import ch.chrummibei.silvercoin.config.UniverseConfig;
import ch.chrummibei.silvercoin.constants.Categories;
import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.components.*;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import ch.chrummibei.silvercoin.universe.entity_systems.TraderSystem;
import ch.chrummibei.silvercoin.universe.item.Recipe;
import ch.chrummibei.silvercoin.universe.position.PricedItemPosition;
import ch.chrummibei.silvercoin.universe.position.YieldingItemPosition;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibrary;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibraryManager;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;

/**
 * Created by brachiel on 21/02/2017.
 */
public class FactoryEntityFactory {
    public static int factorySequence = 0;

    static {
        // Make the behavior tree library parser log
        BehaviorTreeLibraryManager.getInstance().setLibrary(new BehaviorTreeLibrary(BehaviorTreeParser.DEBUG_HIGH));
    }

    public static Entity FactoryEntity(
            UniverseConfig universeConfig,
            Entity market,
            Vector2 position,
            Recipe recipe) {
        Entity entity = new Entity();
        Mappers.market.get(market).addTrader(entity);

        FactoryComponent factory = new FactoryComponent(recipe,
                universeConfig.factory().getRandomInt("goalStock"),
                universeConfig.factory().getRandomDouble("spreadFactor"));
        InventoryComponent inventory = new InventoryComponent();

        Filter filter = new Filter();
        filter.categoryBits = Categories.FACTORY;
        filter.maskBits = Categories.TRANSPORT | Categories.SHIP;
        PhysicsComponent physics = new PhysicsComponent(entity, position, BodyDef.BodyType.StaticBody, 3, filter);

        entity.add(new NamedComponent(recipe.product.getName() + " factory " + factorySequence++));
        entity.add(new WalletComponent(universeConfig.factory().getRandomDouble("startingCredit")));
        entity.add(new MarketAccessComponent(market));
        entity.add(inventory);
        entity.add(new TraderComponent());
        entity.add(factory);
        entity.add(physics);

        if (Universe.DEBUG) {
            entity.add(new LoggerComponent());
        }

        BehaviorTreeLibraryManager behaviorTreeLibraryManager = BehaviorTreeLibraryManager.getInstance();
        entity.add(new AIComponent(behaviorTreeLibraryManager.createBehaviorTree("mods/ai/factory.btree", entity)));

        recipe.ingredients.forEach((ingredient, amount) -> {
            int wantedAmount = amount * factory.goalStock;
            int startingAmount = (int) Math.round(wantedAmount
                    * universeConfig.factory().getRandomDouble("inventoryPerIngredient"));
            Price purchasePrice = new Price(universeConfig.factory()
                    .getRandomDouble("purchasePricePerIngredient"));
            TraderSystem.addPricedPositionToInventory(entity,
                    new PricedItemPosition(ingredient, startingAmount, purchasePrice));
        });

        inventory.positions.put(recipe.product, new YieldingItemPosition(recipe.product));
        return entity;
    }
}
