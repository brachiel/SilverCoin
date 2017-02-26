package ch.chrummibei.silvercoin.universe.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

/**
 * Created by brachiel on 26/02/2017.
 */
public class PhysicsComponent implements Component {
    public BodyDef bodyDef;
    public Body body;
}
