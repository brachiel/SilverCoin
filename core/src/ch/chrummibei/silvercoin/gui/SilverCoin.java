package ch.chrummibei.silvercoin.gui;

import ch.chrummibei.silvercoin.config.Resources;
import ch.chrummibei.silvercoin.config.UniverseConfig;
import ch.chrummibei.silvercoin.gui.widgets.FactoryList;
import ch.chrummibei.silvercoin.gui.widgets.ItemList;
import ch.chrummibei.silvercoin.universe.Universe;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class SilverCoin implements ApplicationListener {
	private Stage stage;
	private Universe universe;
	private UniverseConfig universeConfig;
	private Skin skin;

	BitmapFont font;

	public int WIDTH = 800;
	public int HEIGHT = 400;

	@Override
	public void create () {
	    // We must do this all here and not in the constructor since Gdx.files are not ready during constructor
        universeConfig = new UniverseConfig();
        universe = new Universe(universeConfig);

		stage = new Stage(new ExtendViewport(WIDTH, HEIGHT));
		font = Resources.getDefaultFont();

		skin = new Skin(Gdx.files.internal("skins/uiskin.json"),
                new TextureAtlas(Gdx.files.internal("skins/uiskin.atlas")));


		/*
		batch.setProjectionMatrix(camera.combined);

		// Clear screen
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		TODO:
		batch.begin();
		backgroundSprite.setCenter(WIDTH/2, HEIGHT/2);
		backgroundSprite.setColor(0.3f,0.3f,0.3f,1f);
		backgroundSprite.draw(batch);
		batch.end();


		*/


        FactoryList factoryList = new FactoryList(universe, skin);
        //factoryList.setBounds(0, 0, WIDTH/2, HEIGHT);
        //factoryList.setWidth(WIDTH/2);
		//factoryList.setPosition(5, HEIGHT - 5);

        ScrollPane factoryListScroll = new ScrollPane(factoryList);
        //factoryListScroll.setPosition(0, HEIGHT);
        factoryListScroll.setBounds(5, 5, WIDTH/2-5, HEIGHT-10);

        ItemList itemList = new ItemList(universe, skin);
        //itemList.setPosition(factoryListScroll.getWidth() + 10, HEIGHT - 5);

        Table container = new Table();
		container.setFillParent(true);
        container.add(factoryListScroll).expand().fill();
        container.add(itemList).expand().fill();

        stage.addActor(container);

        Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void render() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		universe.update(deltaTime); // Game Tick
		stage.act(deltaTime); // Render


        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.draw();
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
