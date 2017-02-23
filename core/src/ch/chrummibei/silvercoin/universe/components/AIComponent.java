package ch.chrummibei.silvercoin.universe.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.BehaviorTree;

import java.util.HashMap;

/**
 * Created by brachiel on 24/02/2017.
 */
public class AIComponent implements Component {
    public BehaviorTree<Entity> btree;
    public HashMap<String,String> variables = new HashMap<>();

    public AIComponent(BehaviorTree<Entity> btree) {
        this.btree = btree;
    }
}
