package ch.chrummibei.silvercoin.gui.actors;

import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

/**
 * Drawable actor for factories. Gives different sprites depending on the product.
 */
public class FactoryActor extends Image {
    public static Texture defaultTexture = new Texture(Gdx.files.internal("skins/factory.png"));
    public static Texture shipYardTexture = new Texture(Gdx.files.internal("skins/shipyard.png"));
    public static Texture solarTexture = new Texture(Gdx.files.internal("skins/solar_factory.png"));
    public static TextureRegion defaultTextureRegion = new TextureRegion(defaultTexture);
    Texture texture;
    Body body;
    public Entity factory;
    //int textureRegionNum = -1;

    public FactoryActor(Body body) {
        super();
        this.body = body;
        this.texture = texture;
        this.factory = (Entity) body.getUserData();

        TextureRegion textureRegion;
        switch(Mappers.factory.get(factory).recipe.product.getName()) {
            case "Energy":
                textureRegion = new TextureRegion(solarTexture);
                break;
            case "Transport ship":
                textureRegion = new TextureRegion(shipYardTexture);
                break;
            default:
                textureRegion = defaultTextureRegion;
                break;
        }

        setDrawable(new TextureRegionDrawable(textureRegion));
        setSize(getPrefWidth(), getPrefHeight());
        setOrigin(Align.center);

        setPosition(body.getPosition().x, body.getPosition().y, Align.center);

        // setTouchable(Touchable.enabled);
    }
}
