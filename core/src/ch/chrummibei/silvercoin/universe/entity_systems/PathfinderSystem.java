package ch.chrummibei.silvercoin.universe.entity_systems;

import ch.chrummibei.silvercoin.universe.components.PathfinderComponent;
import ch.chrummibei.silvercoin.universe.components.PhysicsComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by brachiel on 26/02/2017.
 */
public class PathfinderSystem extends IteratingSystem {
    public static final float MAX_VELOCITY = 50;

    public static Family family = Family.all(
                PhysicsComponent.class,
                PathfinderComponent.class
            ).get();

    public PathfinderSystem() {
        super(family);
    }

    public PathfinderSystem(int priority) {
        super(family, priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent physics = Mappers.physics.get(entity);
        PathfinderComponent pathfinder = Mappers.pathfinder.get(entity);

        if (pathfinder.goal == null) return;

        Body body = physics.body;
        Vector2 directPath = pathfinder.goal.cpy().sub(body.getPosition());

        // Check if we're on target
        if (directPath.len() <= pathfinder.precision) {
            physics.body.setLinearVelocity(0, 0);
        } else {
            physics.body.setLinearVelocity(directPath.setLength(MAX_VELOCITY));
            physics.body.setAwake(true);
        }
    }
}
