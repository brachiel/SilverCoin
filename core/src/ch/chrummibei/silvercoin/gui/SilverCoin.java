package ch.chrummibei.silvercoin.gui;

import ch.chrummibei.silvercoin.config.Resources;
import ch.chrummibei.silvercoin.config.UniverseConfig;
import ch.chrummibei.silvercoin.constants.Messages;
import ch.chrummibei.silvercoin.gui.actors.FactoryActor;
import ch.chrummibei.silvercoin.gui.actors.PlayerActor;
import ch.chrummibei.silvercoin.gui.actors.ShipActor;
import ch.chrummibei.silvercoin.gui.hud.BottomBar;
import ch.chrummibei.silvercoin.gui.hud.FactoryList;
import ch.chrummibei.silvercoin.gui.hud.TradeOfferList;
import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.components.ActorComponent;
import ch.chrummibei.silvercoin.universe.components.PhysicsComponent;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
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
    private TradeOfferList tradeOfferList;
    private Table mainContainer;
    private ClickListener actorHoverListener;
    private boolean paused = false;
    private Drawable backgroundDrawable;

    // HUD
    private Stage hudStage;
    private Stack hudOverlay;
    private ScrollPane factoryListScroll;
    private ScrollPane tradeOfferListScroll;
    private BottomBar bottomBar;

    @Override
	public void create() {
	    // We must do this all here and not in the constructor since Gdx.files are not ready during constructor
        universeConfig = new UniverseConfig();
        universe = new Universe(universeConfig);

		stage = new Stage(new ExtendViewport(WIDTH, HEIGHT));
        hudStage = new Stage(new ExtendViewport(WIDTH, HEIGHT));
		font = Resources.getDefaultFont();

		skin = new Skin(Gdx.files.internal("skins/neon/neon-ui.json"),
               new TextureAtlas(Gdx.files.internal("skins/neon/neon-ui.atlas")));

		debugRenderer = new Box2DDebugRenderer();

        createMainContainer();
        createHUD();

        connectMessageHandler();
        connectEventListener();

        addBodyActors();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(universe.playerSystem);
        multiplexer.addProcessor(hudStage);
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);


        // Prepare and set background
        Texture backgroundTexture = new Texture(Gdx.files.internal("images/ngc253.jpg"));
        TextureRegion backgroundRegion = new TextureRegion(backgroundTexture);
        backgroundDrawable = new TextureRegionDrawable(backgroundRegion)
                .tint(new Color(0.4f, 0.4f, 0.4f, 1f));
    }

    private void addBodyActors() {
        // Add Ships
        Array<Body> bodies = new Array<>();
        universe.box2dWorld.getBodies(bodies);
        for (Body body : bodies) {
            Entity entity = (Entity) body.getUserData();

            if (Mappers.player.has(entity)) {
                PlayerActor playerActor = new PlayerActor(body);
                stage.addActor(playerActor);
            } else if (Mappers.factory.has(entity)) {
                FactoryActor factoryActor = new FactoryActor(body);
                stage.addActor(factoryActor);
                factoryActor.addListener(actorHoverListener);
            }
        }
    }

    private void connectEventListener() {
        hudStage.addListener(new ClickListener() {
            @Override
            public boolean keyDown(InputEvent event, int keyCode) {
                switch (keyCode) {
                    case Input.Keys.TAB:
                        showEnvironmentInfoHud();
                        return true;
                    case Input.Keys.T:
                        toggleTradeOffers();
                        return true;
                    case Input.Keys.LEFT:
                        stage.getCamera().translate(-20, 0, 0);
                        return true;
                    case Input.Keys.RIGHT:
                        stage.getCamera().translate(20, 0, 0);
                        return true;
                    case Input.Keys.UP:
                        stage.getCamera().translate(0, 20, 0);
                        return true;
                    case Input.Keys.DOWN:
                        stage.getCamera().translate(0, -20, 0);
                        return true;
                }
                return super.keyDown(event, keyCode);
            }

            @Override
            public boolean keyUp(InputEvent event, int keyCode) {
                if (keyCode == Input.Keys.TAB) {
                    hideEnvironmentInfoHud();
                    return true;
                }
                return super.keyUp(event, keyCode);
            }


        });

        stage.addListener(new ClickListener() {
            final int scrollButton = Input.Buttons.MIDDLE;
            float touchDownX;
            float touchDownY;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button != scrollButton) return false;
                touchDownX = x;
                touchDownY = y;
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                //super.touchDragged(event, x, y, pointer);
                stage.getCamera().translate(touchDownX-x, touchDownY-y, 0);
                /* We do not touch the touch down points since with the moving of the camera, they are
                 * in the right spot already :). */
                //touchDownX = x;
                //touchDownY = y;
            }
        });

        actorHoverListener = new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);

                if (! (event.getTarget() instanceof FactoryActor)) return;
                FactoryActor factoryActor = (FactoryActor) event.getTarget();
                bottomBar.setDisplayedFactory(factoryActor.factory);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);

                if (! (event.getTarget() instanceof FactoryActor)) return;
                bottomBar.clearDisplayedFactory();
            }
        };
    }


    private void toggleTradeOffers() {
        if (tradeOfferListScroll.isVisible()) {
            hideTradeOffers();
        } else {
            showTradeOffers();
        }
    }

    private void showTradeOffers() {
        hideEnvironmentInfoHud();
        hudStage.setScrollFocus(tradeOfferListScroll);
        tradeOfferListScroll.setVisible(true);
    }

    private void hideTradeOffers() {
        tradeOfferListScroll.setVisible(false);
        hudStage.unfocus(factoryListScroll);
    }

    private void showEnvironmentInfoHud() {
        hideTradeOffers();
        factoryListScroll.setVisible(true);
        hudStage.setScrollFocus(factoryListScroll);
    }

    private void hideEnvironmentInfoHud() {
        factoryListScroll.setVisible(false);
        hudStage.unfocus(factoryListScroll);
    }

    private void createHUD() {

        /*
        VerticalGroup vSplitter = new VerticalGroup();
        ItemList itemList = new ItemList(universe, skin);
        vSplitter.addActor(itemList);

        tradeOfferList = new TradeOfferList(universe, skin);
        */

        Table hud = new Table();
        hud.setPosition(0, 0);
        hud.setOrigin(Align.bottom);
        hud.align(Align.bottom);
        hud.setFillParent(true);

        hudOverlay = new Stack();

        TradeOfferList tradeOfferList = new TradeOfferList(skin);
        tradeOfferListScroll = new ScrollPane(tradeOfferList, skin);
        tradeOfferListScroll.setVisible(false);
        hudOverlay.add(tradeOfferListScroll);


        FactoryList factoryList = new FactoryList(skin);
        factoryListScroll = new ScrollPane(factoryList, skin);
        factoryListScroll.setVisible(false);
        hudOverlay.add(factoryListScroll);

        hud.add(hudOverlay).fill();

        hud.row();
        bottomBar = new BottomBar(skin);
        hud.add(bottomBar).align(Align.bottom).expandX().fillX();

        hudStage.addActor(hud);
        //hudStage.setDebugAll(true);
    }

    private void createMainContainer() {
        mainContainer = new Table();

        // Pack everything to the main container
        mainContainer.setFillParent(true);

        stage.addActor(mainContainer);
    }

    private void connectMessageHandler() {
        universe.messageDispatcher.addListeners(msg -> {
                    switch (msg.message) {
                        case Messages.TRANSPORT_SENT:
                            Entity transport = (Entity) msg.extraInfo;
                            PhysicsComponent physics = Mappers.physics.get(transport);

                            // We need to create a new transport actor
                            ShipActor ship = new ShipActor(physics.body);

                            // Connect ship and actor
                            ship.setUserObject(transport);
                            transport.add(new ActorComponent(ship));

                            stage.addActor(ship);
                            break;
                        case Messages.TRANSPORT_ARRIVED:
                            transport = (Entity) msg.extraInfo;

                            // We need to destroy the transport actor
                            ActorComponent actor = Mappers.actor.get(transport);
                            if (actor == null) {
                                System.out.println("No actor. Something is wrong.");
                            }
                            actor.actor.remove();
                            transport.remove(ActorComponent.class);
                            break;
                    }
                    return true;
                },
                Messages.TRANSPORT_SENT,
                Messages.TRANSPORT_ARRIVED);
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
        hudStage.act(deltaTime);

        // Draw background
        Camera camera = stage.getCamera();
        camera.update();
        Batch batch = stage.getBatch();
        batch.begin();
        batch.setProjectionMatrix(camera.projection);
        backgroundDrawable.draw(batch, -WIDTH/2, -HEIGHT/2, WIDTH, HEIGHT);
        batch.end();

		stage.draw();
        hudStage.draw();

		//debugRenderer.render(universe.box2dWorld, stage.getCamera().combined);
        if (! paused) {
            universe.update(deltaTime); // Game Tick
        }
	}

    @Override
	public void pause() {
        paused = true;
	}

	@Override
	public void resume() {
        paused = false;
        universe.update(0.1f); // Game Tick
	}

	@Override
	public void dispose() {

	}
}
