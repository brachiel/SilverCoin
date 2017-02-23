package ch.chrummibei.silvercoin.universe.entity_systems;

import ch.chrummibei.silvercoin.universe.components.AIComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

/**
 * Created by brachiel on 24/02/2017.
 */
public class AISystem extends IteratingSystem {
    private static Family family = Family.all(AIComponent.class).get();

    public AISystem() {
        super(family);
    }

    public AISystem(int priority) {
        super(family, priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AIComponent ai = Mappers.ai.get(entity);
        ai.btree.step();
    }
}
