package ch.chrummibei.silvercoin.universe.entity_systems;

import ch.chrummibei.silvercoin.universe.components.*;
import com.badlogic.ashley.core.ComponentMapper;

/**
 * Convenience static class with component mappers. These are needed for lookup-complexity O(1)
 */
public class Mappers {
    public static final ComponentMapper<FactoryComponent> factory = ComponentMapper.getFor(FactoryComponent.class);
    public static final ComponentMapper<MarketComponent> market = ComponentMapper.getFor(MarketComponent.class);
    public static final ComponentMapper<MarketSightComponent> marketSight = ComponentMapper.getFor(MarketSightComponent.class);
    public static final ComponentMapper<NamedComponent> named = ComponentMapper.getFor(NamedComponent.class);
    public static final ComponentMapper<TraderComponent> trader = ComponentMapper.getFor(TraderComponent.class);
    public static final ComponentMapper<WalletComponent> wallet = ComponentMapper.getFor(WalletComponent.class);
    public static final ComponentMapper<InventoryComponent> inventory = ComponentMapper.getFor(InventoryComponent.class);
    public static final ComponentMapper<BigSpenderComponent> bigSpender = ComponentMapper.getFor(BigSpenderComponent.class);
}
