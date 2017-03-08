package ch.chrummibei.silvercoin.universe.entity_systems;

import ch.chrummibei.silvercoin.universe.components.*;
import com.badlogic.ashley.core.ComponentMapper;

/**
 * Convenience static class with component mappers. These are needed for lookup-complexity O(1)
 */
public class Mappers {
    public static final ComponentMapper<ActorComponent> actor = ComponentMapper.getFor(ActorComponent.class);
    public static final ComponentMapper<FactoryComponent> factory = ComponentMapper.getFor(FactoryComponent.class);
    public static final ComponentMapper<NamedComponent> named = ComponentMapper.getFor(NamedComponent.class);
    public static final ComponentMapper<TraderComponent> trader = ComponentMapper.getFor(TraderComponent.class);
    public static final ComponentMapper<WalletComponent> wallet = ComponentMapper.getFor(WalletComponent.class);
    public static final ComponentMapper<InventoryComponent> inventory =
                                ComponentMapper.getFor(InventoryComponent.class);
    public static final ComponentMapper<BigSpenderComponent> bigSpender =
                                ComponentMapper.getFor(BigSpenderComponent.class);
    public static final ComponentMapper<LoggerComponent> logger = ComponentMapper.getFor(LoggerComponent.class);
    public static final ComponentMapper<AIComponent> ai = ComponentMapper.getFor(AIComponent.class);
    public static final ComponentMapper<PhysicsComponent> physics = ComponentMapper.getFor(PhysicsComponent.class);
    public static final ComponentMapper<PlayerComponent> player = ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<PathfinderComponent> pathfinder =
                                ComponentMapper.getFor(PathfinderComponent.class);
    public static final ComponentMapper<TransportComponent> transport =
                                ComponentMapper.getFor(TransportComponent.class);
    public static final ComponentMapper<TradeSphereComponent> tradeSphere =
                                ComponentMapper.getFor(TradeSphereComponent.class);
}
