package ch.chrummibei.silvercoin.ai.tasks.factory;

import ch.chrummibei.silvercoin.universe.components.FactoryComponent;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;

/**
 * Created by brachiel on 24/02/2017.
 */
public class ChangeSpreadTask extends LeafTask<Entity> {
    @TaskAttribute(required = true)
    public float spreadChangeFactor;

    public ChangeSpreadTask() {
        this(0);
    }
    public ChangeSpreadTask(float spreadChange) {
        this.spreadChangeFactor = spreadChange;
    }

    @Override
    public Status execute() {
        FactoryComponent factory = Mappers.factory.get(this.getObject());

        if (factory.priceSpreadFactor * spreadChangeFactor > 1) {
            factory.priceSpreadFactor *= spreadChangeFactor;
            return Status.SUCCEEDED;
        } else {
            return Status.FAILED;
        }
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        ((ChangeSpreadTask) task).spreadChangeFactor = spreadChangeFactor;
        return task;
    }
}
