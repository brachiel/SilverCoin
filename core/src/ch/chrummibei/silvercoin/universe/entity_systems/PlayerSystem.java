package ch.chrummibei.silvercoin.universe.entity_systems;

import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.components.PathfinderComponent;
import ch.chrummibei.silvercoin.universe.components.PhysicsComponent;
import ch.chrummibei.silvercoin.universe.components.PlayerComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by brachiel on 26/02/2017.
 */
public class PlayerSystem extends IteratingSystem implements InputProcessor {
    public static Family family = Family.all(PlayerComponent.class).get();

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
        Vector2 direction = new Vector2(1,0).rotateRad(body.getAngle());

        final float straightImpulse = 100f;
        final float angularImpulse = 50f;

        // Trivial input handling
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            // Accelerate
            physics.body.applyLinearImpulse(direction.cpy().setLength(straightImpulse), body.getPosition(), true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            // Decelerate
            physics.body.applyLinearImpulse(direction.cpy().setLength(straightImpulse).rotate(180), body.getPosition(), true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            physics.body.applyAngularImpulse(angularImpulse, true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            physics.body.applyAngularImpulse(-angularImpulse, true);
        }

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        PathfinderComponent pathfinder = Mappers.pathfinder.get(Universe.player);

        if (button == Input.Buttons.RIGHT) {
            // TODO: Take scrolled camera into consideration
            pathfinder.goal = new Vector2(screenX/2, (Gdx.graphics.getHeight()-screenY)/2);
            return true;
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
