package ch.chrummibei.silvercoin.gui.actors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

/**
 * Created by brachiel on 27/02/2017.
 */
public class FactoryActor extends Image {
    public static Texture defaultTexture = new Texture(Gdx.files.internal("skins/market.png"));
    public static TextureRegion defaultTextureRegion = new TextureRegion(defaultTexture);
    Texture texture;
    Body body;
    public Entity market;
    //int textureRegionNum = -1;

    public FactoryActor(Body body) {
        this(body, defaultTextureRegion);
    }

    public FactoryActor(Body body, TextureRegion textureRegion) {
        super();
        this.body = body;
        this.texture = texture;
        this.market = (Entity) body.getUserData();

        setDrawable(new TextureRegionDrawable(textureRegion));
        setSize(getPrefWidth(), getPrefHeight());
        setOrigin(Align.center);

        setPosition(body.getPosition().x, body.getPosition().y, Align.center);

        // setTouchable(Touchable.enabled);
    }
}
