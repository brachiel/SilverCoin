package ch.chrummibei.silvercoin.universe.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by brachiel on 20/02/2017.
 */
public class NamedComponent implements Component {
    public final String name;

    public NamedComponent(String name) {
        this.name = name;
    }
}
