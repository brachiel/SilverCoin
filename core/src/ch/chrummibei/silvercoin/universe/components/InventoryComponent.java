package ch.chrummibei.silvercoin.universe.components;

import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.position.YieldingItemPosition;
import com.badlogic.ashley.core.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brachiel on 20/02/2017.
 */
public class InventoryComponent implements Component {
    public Map<Item,YieldingItemPosition> positions = new HashMap<>();
}
