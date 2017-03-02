package ch.chrummibei.silvercoin.universe.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by brachiel on 27/02/2017.
 */
public class PathfinderComponent implements Component {
    public Vector2 goal;
    public float precision = 3f; // Radius necessary to turn off engines

    public PathfinderComponent(Vector2 goal) {
        this.goal = goal;
    }
}
