package ch.chrummibei.silvercoin.universe.entity_systems;

import ch.chrummibei.silvercoin.universe.components.InventoryComponent;
import ch.chrummibei.silvercoin.universe.components.PhysicsComponent;
import ch.chrummibei.silvercoin.universe.components.TraderComponent;
import ch.chrummibei.silvercoin.universe.components.WalletComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

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
        PhysicsComponent physics = Mappers.physics.get(entity);

        Body body = physics.body;
        Vector2 velocity = body.getLinearVelocity();
        Vector2 direction = new Vector2(1,0).rotateRad(body.getAngle());

        final float straightImpulse = 100f;
        final float angularImpulse = 50f;

        System.out.println(direction);

        // Trivial input handling
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            // Accelerate
            physics.body.applyLinearImpulse(direction.cpy().setLength(straightImpulse), body.getPosition(), true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            // Decelerate
            physics.body.applyLinearImpulse(direction.cpy().setLength(straightImpulse).rotate(180), body.getPosition(), true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            physics.body.applyAngularImpulse(angularImpulse, true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            physics.body.applyAngularImpulse(-angularImpulse, true);
        }

    }
}
