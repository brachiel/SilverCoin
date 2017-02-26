package ch.chrummibei.silvercoin.universe.entity_factories;

import ch.chrummibei.silvercoin.universe.components.*;
import ch.chrummibei.silvercoin.universe.credit.TotalValue;
import com.badlogic.ashley.core.Entity;

/**
 * Created by brachiel on 26/02/2017.
 */
public class PlayerEntityFactory {
    public static Entity Player() {
        Entity entity = new Entity();
        entity.add(new WalletComponent(new TotalValue(1000)));
        entity.add(new InventoryComponent());
        entity.add(new TraderComponent());
        entity.add(new PhysicsComponent());
        entity.add(new NamedComponent("Player"));

        return entity;
    }
}
