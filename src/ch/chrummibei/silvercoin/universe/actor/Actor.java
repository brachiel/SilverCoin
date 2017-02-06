package ch.chrummibei.silvercoin.universe.actor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by brachiel on 03/02/2017.
 */
public interface Actor {
    void tick(long timeStepMillis);
}
