package ch.chrummibei.silvercoin.gui;

import ch.chrummibei.silvercoin.config.Resources;
import ch.chrummibei.silvercoin.config.UniverseConfig;
import ch.chrummibei.silvercoin.gui.widgets.FactoryList;
import ch.chrummibei.silvercoin.gui.widgets.ItemList;
import ch.chrummibei.silvercoin.universe.Universe;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class SilverCoin implements ApplicationListener {
	private Stage stage;
	private Universe universe;
	private UniverseConfig universeConfig;
	private Skin skin;

	BitmapFont font;

	public int WIDTH = 800;
	public int HEIGHT = 400;

    private Box2DDebugRenderer debugRenderer;
    private Camera debugCamera;

    @Override
	public void create () {
	    // We must do this all here and not in the constructor since Gdx.files are not ready during constructor
        universeConfig = new UniverseConfig();
        universe = new Universe(universeConfig);

		stage = new Stage(new ExtendViewport(WIDTH, HEIGHT));
		font = Resources.getDefaultFont();

		skin = new Skin(Gdx.files.internal("skins/uiskin.json"),
                new TextureAtlas(Gdx.files.internal("skins/uiskin.atlas")));

		debugRenderer = new Box2DDebugRenderer();

        FactoryList factoryList = new FactoryList(universe, skin);

        ScrollPane factoryListScroll = new ScrollPane(factoryList);
        factoryListScroll.setBounds(5, 5, WIDTH/2-5, HEIGHT-10);

        Table container = new Table();

        VerticalGroup vSplitter = new VerticalGroup();
        universe.getMarketComponents().forEach(market -> {
            ItemList itemList = new ItemList(universe, market, skin);
            vSplitter.addActor(itemList);
        });

        // Pack everything to the main container
		container.setFillParent(true);
        container.add(factoryListScroll).width(WIDTH/2).fill();
        container.add(vSplitter).expand().fill();

        // Prepare and set background
        Texture backgroundTexture = new Texture(Gdx.files.internal("images/ngc253.jpg"));
        TextureRegion backgroundRegion = new TextureRegion(backgroundTexture);
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(backgroundRegion);
        container.background(backgroundDrawable.tint(new Color(0.4f,0.4f,0.4f,1f)));

        // stage.addActor(container);

        Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void render() {
		float deltaTime = Gdx.graphics.getDeltaTime();

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(deltaTime); // Update actors
		stage.draw();

		debugRenderer.render(universe.box2dWorld, stage.getCamera().combined);

        universe.update(deltaTime); // Game Tick

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}
}
