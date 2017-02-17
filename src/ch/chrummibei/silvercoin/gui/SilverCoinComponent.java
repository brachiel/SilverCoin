package ch.chrummibei.silvercoin.gui;

import ch.chrummibei.silvercoin.config.UniverseConfig;
import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.actor.TimeStepActionActor;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Main GUI component of SilverCoin
 */
public class SilverCoinComponent extends Canvas implements Runnable, TimeStepActionActor {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static final int SCALE = 2;
    private static final double TARGET_TPS = 20.0;
    private static final double TARGET_FPS = 10.0;

    private final UniverseConfig universeConfig;
    private final Universe universe;

    private final Screen screen;
    private final WritableRaster screenRaster;
    private final BufferedImage img;
    private BufferStrategy bufferStrategy;
    private boolean running = false;
    private Thread thread;
    private final Map<Consumer<Long>, Timekeeper> timeStepActorAction = new HashMap<>();

    private long totalTime = 0;
    private long totalTickCount = 0;
    private long totalRenderCount = 0;


    public SilverCoinComponent() {
        Dimension size = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
        GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();

        img = graphicsConfiguration.createCompatibleImage(WIDTH, HEIGHT, Transparency.BITMASK);
        screenRaster = img.getRaster();
        screen = new Screen(screenRaster);

        Graphics g = img.getGraphics();
        g.setColor(Color.green);
        g.fillOval(200, 100, 200, 200);
        g.dispose();

        // Read config file
        universeConfig = new UniverseConfig();
        universe = new Universe(universeConfig);


        // Render universe on screen
        // Setup rendering every 1000 milliseconds
        this.addAction(this::render, Math.round(1000/TARGET_FPS));

        this.addAction(this::reportFPS, Math.round(1000/TARGET_TPS));

    }

    private void reportFPS(Long timeDiffMillis) {
        totalTime += timeDiffMillis;
        System.out.println("FPS: " + (totalRenderCount * 1000) / totalTime);
        System.out.println("TPS: " + (totalTickCount * 1000) / totalTime);
    }

    public Universe getUniverse() {
        return universe;
    }

    public void start() {
        if (running)
            return;
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        if (!running)
            return;
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long lastTickMillis = System.currentTimeMillis();
        totalTickCount = 0;
        totalRenderCount = 0;

        createBufferStrategy(3);
        bufferStrategy = getBufferStrategy();

        while (running) {
            long nowMillis = System.currentTimeMillis();

            tick(nowMillis - lastTickMillis); // This might take a while
            // render(); // Render is solved through an TimeStepActionActor

            lastTickMillis = System.currentTimeMillis();
            // We have to sleep currentTime + 1000/targetTicksPerSecond - now
            try {
                long millisToSleep = (long) (nowMillis + 1000/TARGET_TPS - lastTickMillis);

                if (millisToSleep > 0) {
                    Thread.sleep(millisToSleep);
                } else {
                    System.out.println("Trouble keeping up...");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                running = false;
            }

            ++totalTickCount;
        }
    }

    /*
        render is called by this being a TimeStepActionActor and the render
        method being registered as an action.
     */
    private void render(long timeDiffMillis) {
        screen.render(universe);
        //screen.testScreen();

        // Render screen on raster
        img.getRaster().setRect(screenRaster);

        Graphics graphics = bufferStrategy.getDrawGraphics();
        graphics.setColor(Color.black);
        graphics.fillRect(0, 0, getWidth(), getHeight());
        graphics.setColor(Color.red);
        graphics.fillOval(400, 300, 400, 400);
        graphics.drawImage(img, 0, 0, getWidth(), getHeight(), null);
        graphics.dispose();
        bufferStrategy.show();

        ++totalRenderCount;
    }

    @Override
    public void addAction(Consumer<Long> action, long periodicity) {
        timeStepActorAction.put(action, new Timekeeper(periodicity));
    }

    @Override
    public Map<Consumer<Long>, Timekeeper> getTimedActions() {
        return timeStepActorAction;
    }

    public void tick(long timeDiffMillis) {
        universe.tick(timeDiffMillis);
        TimeStepActionActor.super.tick(timeDiffMillis); // Check if we need to render
    }
}
