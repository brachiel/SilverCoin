package ch.chrummibei.silvercoin.ai.tasks.factory;

import ch.chrummibei.silvercoin.universe.components.FactoryComponent;
import ch.chrummibei.silvercoin.universe.components.TraderComponent;
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
    public float spreadChange;

    public ChangeSpreadTask() {
        this(0);
    }
    public ChangeSpreadTask(float spreadChange) {
        this.spreadChange = spreadChange;
    }

    @Override
    public Status execute() {
        FactoryComponent factory = Mappers.factory.get(this.getObject());
        TraderComponent trader = Mappers.trader.get(this.getObject());

        if (factory.priceSpreadFactor + spreadChange > 1) {
            factory.priceSpreadFactor += spreadChange;
            return Status.SUCCEEDED;
        } else {
            return Status.FAILED;
        }
    }

    @Override
    protected Task<Entity> copyTo(Task<Entity> task) {
        ((ChangeSpreadTask) task).spreadChange = spreadChange;
        return task;
    }
}
