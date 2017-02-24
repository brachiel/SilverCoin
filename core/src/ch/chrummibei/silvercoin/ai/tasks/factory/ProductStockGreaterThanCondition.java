package ch.chrummibei.silvercoin.ai.tasks.factory;

import ch.chrummibei.silvercoin.universe.components.FactoryComponent;
import ch.chrummibei.silvercoin.universe.entity_systems.FactorySystem;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

/**
 * Created by brachiel on 24/02/2017.
 */
public class ProductStockGreaterThanCondition extends LeafTask<Entity> {
    @TaskAttribute(required = true)
    public float stockToGoalFactor;

    public ProductStockGreaterThanCondition() {
        this(1f);
    }
    public ProductStockGreaterThanCondition(float stockToGoalFactor) {
        this.stockToGoalFactor = stockToGoalFactor;
    }

    @Override
    public Status execute() {
        FactoryComponent factory = Mappers.factory.get(this.getObject());
        return FactorySystem.getProductPosition(this.getObject()).getAmount() / factory.goalStock >= stockToGoalFactor ?
                Status.SUCCEEDED : Status.FAILED;
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        ((ProductStockGreaterThanCondition) task).stockToGoalFactor = stockToGoalFactor;
        return task;
    }
}
