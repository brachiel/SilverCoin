package ch.chrummibei.silvercoin.universe.actor;

import ch.chrummibei.silvercoin.universe.space.Position;

/**
 * A positioned actor must have a way to get and set its position.
 */
interface PositionedActor extends Actor {
    Position getPosition();
    void setPosition(Position position);
}
