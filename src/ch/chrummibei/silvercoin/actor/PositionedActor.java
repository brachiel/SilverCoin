package ch.chrummibei.silvercoin.actor;

import ch.chrummibei.silvercoin.space.Position;

/**
 * Created by brachiel on 03/02/2017.
 */
public abstract class PositionedActor implements Actor {
    Position position;

    public PositionedActor(Position position) {
        setPosition(position);
    }

    public Position getPosition() {
        return position;
    }
    public void setPosition(Position position) {
        this.position = position;
    }
}
