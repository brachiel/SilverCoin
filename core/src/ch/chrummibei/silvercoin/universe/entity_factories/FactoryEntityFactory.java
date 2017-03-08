package ch.chrummibei.silvercoin.universe.entity_factories;

import ch.chrummibei.silvercoin.config.UniverseConfig;
import ch.chrummibei.silvercoin.constants.Categories;
import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.components.*;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.entity_systems.TraderSystem;
import ch.chrummibei.silvercoin.universe.item.Item;
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

import java.util.HashMap;

/**
 * Created by brachiel on 21/02/2017.
 */
public class FactoryEntityFactory {
    public static HashMap<Item,Integer> factorySequences = new HashMap<>();
    public static String[] factoryNameReservoir = {
            "Alpha","Beta","Gamma","Delta","Epsilon","Zeta","Eta","Theta","Iota",
            "Kappa","Lambda", "Mu", "Nu", "Xi", "Omicron", "Pi", "Rho", "Sigma",
            "Tau", "Upsilon", "Phi", "Chi", "Psi", "Omega"};

    static {
        // Make the behavior tree library parser log
        BehaviorTreeLibraryManager.getInstance().setLibrary(new BehaviorTreeLibrary(BehaviorTreeParser.DEBUG_HIGH));
    }

    public static String getNextName(Item item) {
        Integer currentSequence = factorySequences.get(item);
        if (currentSequence == null) {
            factorySequences.put(item, 1);
            return factoryNameReservoir[0];
        } else {
            factorySequences.put(item, currentSequence + 1);
            return factoryNameReservoir[currentSequence % factoryNameReservoir.length];
        }
    }

    public static Entity FactoryEntity(
            UniverseConfig universeConfig,
            Vector2 position,
            Recipe recipe) {
        Entity entity = new Entity();

        FactoryComponent factory = new FactoryComponent(recipe,
                universeConfig.factory().getRandomInt("goalStock"),
                universeConfig.factory().getRandomDouble("spreadFactor"));
        InventoryComponent inventory = new InventoryComponent();

        Filter filter = new Filter();
        filter.categoryBits = Categories.FACTORY;
        filter.maskBits = Categories.TRANSPORT | Categories.SHIP;
        PhysicsComponent physics = new PhysicsComponent(entity, position, BodyDef.BodyType.KinematicBody, 3, filter);
        entity.add(new NamedComponent(recipe.product.getName() + " " + getNextName(recipe.product).toLowerCase()));
        entity.add(new WalletComponent(universeConfig.factory().getRandomDouble("startingCredit")));
        entity.add(inventory);
        entity.add(new TraderComponent());
        entity.add(factory);
        entity.add(physics);
        entity.add(new TradeSphereComponent(physics, 500));

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
