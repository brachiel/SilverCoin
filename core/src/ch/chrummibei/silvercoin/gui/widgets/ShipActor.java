package ch.chrummibei.silvercoin.gui.widgets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

/**
 * Created by brachiel on 27/02/2017.
 */
public class ShipActor extends Image {
    Texture texture;
    TextureRegion[] textureRegions = new TextureRegion[1];
    Body body;
    //int textureRegionNum = -1;

    public ShipActor(Body body, Texture texture) {
        super();
        this.body = body;
        this.texture = texture;

        this.textureRegions[0] = new TextureRegion(texture, 0, 0, 7, 11);
        //this.textureRegions[1] = new TextureRegion(texture, 7, 0, 9, 11);
        //this.textureRegions[2] = new TextureRegion(texture, 7+9, 0, 9, 10);

        setDrawable(new TextureRegionDrawable(textureRegions[0]));
        setSize(getPrefWidth(), getPrefHeight());
        setOrigin(Align.center);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        //setDrawable(new TextureRegionDrawable(textureRegions[textureRegionNum]));

        /* Not a good idea
        int regionNumber = (360 + (int) Math.floor(MathUtils.radiansToDegrees * body.getAngle() / 30)) % 3;
        if (textureRegionNum != regionNumber) {
            textureRegionNum = regionNumber;
            setDrawable(new TextureRegionDrawable(textureRegions[textureRegionNum]));
        }
        setOrigin(Align.center);
        setSize(getPrefWidth(), getPrefHeight());
        */

        setRotation(90 + MathUtils.radiansToDegrees * body.getAngle());
        setPosition(body.getPosition().x, body.getPosition().y, Align.center);
    }
}
