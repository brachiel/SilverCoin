package ch.chrummibei.silvercoin.gui;

import ch.chrummibei.silvercoin.config.Resources;
import ch.chrummibei.silvercoin.config.UniverseConfig;
import ch.chrummibei.silvercoin.constants.Messages;
import ch.chrummibei.silvercoin.gui.actors.FactoryActor;
import ch.chrummibei.silvercoin.gui.actors.MarketActor;
import ch.chrummibei.silvercoin.gui.actors.ShipActor;
import ch.chrummibei.silvercoin.gui.widgets.FactoryList;
import ch.chrummibei.silvercoin.gui.widgets.ItemList;
import ch.chrummibei.silvercoin.gui.widgets.TradeOfferList;
import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.components.ActorComponent;
import ch.chrummibei.silvercoin.universe.components.PhysicsComponent;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
    private Table hud;
    private ClickListener marketHoverListener;

    @Override
	public void create() {
	    // We must do this all here and not in the constructor since Gdx.files are not ready during constructor
        universeConfig = new UniverseConfig();
        universe = new Universe(universeConfig);

		stage = new Stage(new ExtendViewport(WIDTH, HEIGHT));
		font = Resources.getDefaultFont();

		skin = new Skin(Gdx.files.internal("skins/uiskin.json"),
               new TextureAtlas(Gdx.files.internal("skins/uiskin.atlas")));

		debugRenderer = new Box2DDebugRenderer();

        createMainContainer();
        createHUD();

        connectMessageHandler();
        connectEventListener();

        addBodyActors();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(universe.playerSystem);
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void addBodyActors() {
        // Add Ships
        Texture marketTexture = new Texture(Gdx.files.internal("skins/market.png"));

        Array<Body> bodies = new Array<>();
        universe.box2dWorld.getBodies(bodies);
        for (Body body : bodies) {
            Entity entity = (Entity) body.getUserData();

            if (Mappers.market.has(entity)) {
                MarketActor marketActor = new MarketActor(body, marketTexture);
                stage.addActor(marketActor);

                marketActor.addListener(marketHoverListener);
            } else if (Mappers.factory.has(entity)) {
                FactoryActor factoryActor = new FactoryActor(body);
                stage.addActor(factoryActor);
            }
        }
    }

    private void connectEventListener() {
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keyCode) {
                if (keyCode == Input.Keys.TAB) {
                    hud.setVisible(true);
                }
                return super.keyDown(event, keyCode);
            }

            @Override
            public boolean keyUp(InputEvent event, int keyCode) {
                if (keyCode == Input.Keys.TAB) {
                    hud.setVisible(false);
                }
                return super.keyUp(event, keyCode);
            }
        });


        marketHoverListener = new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);

                if (! (fromActor instanceof MarketActor)) return;

                Entity market = ((MarketActor) fromActor).market;
                universe.messageDispatcher.dispatchMessage(Messages.HOVER_ENTER_MARKET, market);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);

                if (! (toActor instanceof MarketActor)) return;

                Entity market = ((MarketActor) toActor).market;
                universe.messageDispatcher.dispatchMessage(Messages.HOVER_EXIT_MARKET, market);
            }
        };
    }

    private void createHUD() {
        FactoryList factoryList = new FactoryList(universe, skin);

        ScrollPane factoryListScroll = new ScrollPane(factoryList);
        factoryListScroll.setBounds(5, 5, WIDTH/2-5, HEIGHT-10);

        VerticalGroup vSplitter = new VerticalGroup();
        universe.getMarkets().forEach(market -> {
            ItemList itemList = new ItemList(universe, market, skin);
            vSplitter.addActor(itemList);
        });

        tradeOfferList = new TradeOfferList(universe, skin);

        hud = new Table();
        hud.setFillParent(true);
        hud.setVisible(false);
        hud.add(factoryListScroll).width(WIDTH/2).fill();
        hud.add(vSplitter).expand().fill();

        mainContainer.addActor(hud);
    }

    private void createMainContainer() {
        mainContainer = new Table();

        // Pack everything to the main container
        mainContainer.setFillParent(true);

        // Prepare and set background
        Texture backgroundTexture = new Texture(Gdx.files.internal("images/ngc253.jpg"));
        TextureRegion backgroundRegion = new TextureRegion(backgroundTexture);
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(backgroundRegion);
        mainContainer.background(backgroundDrawable.tint(new Color(0.4f,0.4f,0.4f,1f)));
        stage.addActor(mainContainer);
    }

    private void connectMessageHandler() {
        universe.messageDispatcher.addListeners(msg -> {
                    switch (msg.message) {
                        case Messages.HOVER_ENTER_MARKET:
                        case Messages.PLAYER_JOINED_MARKET:
                            if (tradeOfferList.market != null) return true;
                            tradeOfferList.market = Mappers.market.get((Entity) msg.extraInfo);
                            hud.add(tradeOfferList);
                            break;
                        case Messages.HOVER_EXIT_MARKET:
                        case Messages.PLAYER_LEFT_MARKET:
                            if (tradeOfferList.market == null) return true;
                            tradeOfferList.market = null;
                            hud.removeActor(tradeOfferList);
                            break;
                        case Messages.TRANSPORT_SENT:
                            System.out.println("Transport SENT");
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
                            System.out.println("Transport ARRIVED");
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
                Messages.PLAYER_JOINED_MARKET,
                Messages.PLAYER_LEFT_MARKET,
                Messages.HOVER_ENTER_MARKET,
                Messages.HOVER_EXIT_MARKET,
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
		stage.draw();

		//debugRenderer.render(universe.box2dWorld, stage.getCamera().combined);

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
