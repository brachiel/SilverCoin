package ch.chrummibei.silvercoin.gui.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

/**
 * Created by brachiel on 27/02/2017.
 */
public class ShipActor extends Image {
    public static Texture defaultTexture = new Texture(Gdx.files.internal("skins/ship.png"));
    public static TextureRegion defaultTextureRegion = new TextureRegion(defaultTexture, 0, 0, 7, 11);
    public static ParticleEffect staticParticleEffect;
    public static ParticleEffectPool particleEffectPool;

    static {
        staticParticleEffect = new ParticleEffect();
        staticParticleEffect.load(Gdx.files.internal("skins/ship_particle.p"), Gdx.files.internal("skins"));
        particleEffectPool = new ParticleEffectPool(staticParticleEffect, 1, 20);
    }

    Texture texture;
    Body body;
    ParticleEffectPool.PooledEffect particleEffect;

    float lastDeltaTime;
    //int textureRegionNum = -1;


    public ShipActor(Body body) {
        this(body, defaultTexture);
    }

    public ShipActor(Body body, Texture texture) {
        super();
        this.body = body;
        this.texture = texture;

        setDrawable(new TextureRegionDrawable(defaultTextureRegion));
        setSize(getPrefWidth(), getPrefHeight());
        setOrigin(Align.center);

        particleEffect = null;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        float bodyAngle = MathUtils.radiansToDegrees * body.getAngle();
        setRotation(90 + bodyAngle);
        setPosition(body.getPosition().x, body.getPosition().y, Align.center);

        /* Obtain particle effect */
        if (particleEffect == null) {
            particleEffect = particleEffectPool.obtain();
        }

        Vector2 enginePosition = body.getWorldPoint(new Vector2(-2,0));
        particleEffect.setPosition(enginePosition.x, enginePosition.y);
        // Rotate the emitters
        particleEffect.getEmitters().forEach(emitter -> {
            ParticleEmitter.ScaledNumericValue angle = emitter.getAngle();
            angle.setHigh(bodyAngle + 225, bodyAngle + 135);
            angle.setLow(bodyAngle + 175, bodyAngle + 185);
        });

        lastDeltaTime = delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        /* Draw particle effect */
        particleEffect.draw(batch, lastDeltaTime);
        if (particleEffect.isComplete()) {
            particleEffect.free();
            particleEffect = null;
        }
    }
}
