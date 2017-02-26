package ch.chrummibei.silvercoin.universe.entity_systems;

import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.components.InventoryComponent;
import ch.chrummibei.silvercoin.universe.components.PhysicsComponent;
import ch.chrummibei.silvercoin.universe.components.TraderComponent;
import ch.chrummibei.silvercoin.universe.components.WalletComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by brachiel on 26/02/2017.
 */
public class PlayerSystem extends IteratingSystem {
    public static Family family = Family.all(
                PhysicsComponent.class,
                WalletComponent.class,
                InventoryComponent.class,
                TraderComponent.class
            ).get();

    public PlayerSystem() {
        super(family);
    }

    public PlayerSystem(int priority) {
        super(family, priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (Universe.getRandomDouble(0,1) < 0.2) {
            PhysicsComponent physics = Mappers.physics.get(entity);
            Vector2 position = physics.body.getPosition();

            // Nudge
            physics.body.applyLinearImpulse(
                    (float) Universe.getRandomDouble(-2,2),
                    (float) Universe.getRandomDouble(-2,2),
                    position.x, position.y, true);
        }
    }
}
