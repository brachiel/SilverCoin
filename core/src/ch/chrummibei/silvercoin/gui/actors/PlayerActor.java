package ch.chrummibei.silvercoin.gui.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by brachiel on 27/02/2017.
 */
public class PlayerActor extends ShipActor {
    public static ParticleEffect staticParticleEffect;
    public static ParticleEffectPool particleEffectPool;

    static {
        staticParticleEffect = new ParticleEffect();
        staticParticleEffect.load(Gdx.files.internal("skins/own_ship_particle.p"), Gdx.files.internal("skins"));
        particleEffectPool = new ParticleEffectPool(staticParticleEffect, 1, 20);
    }

    public PlayerActor(Body body) {
        this(body, defaultTexture);
    }

    public PlayerActor(Body body, Texture texture) {
        super(body, texture);

        Drawable tintedShip = new TextureRegionDrawable(defaultTextureRegion).tint(new Color(1f, 0.57f, 0.99f, 1));
        setDrawable(tintedShip);

        /* Obtain particle effect */
        if (particleEffect == null) {
            particleEffect = particleEffectPool.obtain();
        }
    }
}
