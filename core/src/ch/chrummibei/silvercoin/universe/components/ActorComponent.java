package ch.chrummibei.silvercoin.universe.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by brachiel on 01/03/2017.
 */
public class ActorComponent implements Component {
    public Actor actor;

    public ActorComponent(Actor actor) {
        this.actor = actor;
    }
}
