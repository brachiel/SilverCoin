package ch.chrummibei.silvercoin.gui;

import ch.chrummibei.silvercoin.universe.actor.TimeStepActionActor;
import ch.chrummibei.silvercoin.universe.space.Universe;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * Main GUI component of SilverCoin
 */
public class SilverCoinComponent extends Canvas implements Runnable, TimeStepActionActor {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 200;
    private static final int SCALE = 4;
    private static final double TARGET_TPS = 10.0;
    private static final double TARGET_FPS = 1;

    private final Universe universe;

    private final Screen screen;
    private final BufferedImage img;
    private final int[] pixels;
    private boolean running = false;
    private Thread thread;
    private final Map<Consumer<Long>, Timekeeper> timestepActorAction = new HashMap<>();


    public SilverCoinComponent() {
        Dimension size = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        screen = new Screen(WIDTH, HEIGHT);
        img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();

        universe = new Universe();

        // Setup rendering every 1000 milliseconds
        this.addAction(this::render, Math.round(1000*TARGET_FPS));
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
        long totalTicks = 0;

        while (running && totalTicks < 1000) {
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

            ++totalTicks;
        }
    }

    /*
        render is called by this being a TimeStepActionActor and the render
        method being registered as an action.
     */
    private void render(long timeDiffMillis) {
        // Render universe on screen
        BufferStrategy bufferStrategy = getBufferStrategy();
        if (bufferStrategy == null) {
            createBufferStrategy(3);
            bufferStrategy = getBufferStrategy();
        }

        screen.testScreen();

        //screen.render(universe);

        // Render screen pixels on BufferImage
        IntStream.rangeClosed(0, pixels.length-1).parallel().forEach(
                i -> pixels[i] = screen.pixels[i]
        );

        Graphics graphics = bufferStrategy.getDrawGraphics();
        graphics.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
        graphics.drawImage(img, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
        graphics.dispose();
        bufferStrategy.show();
    }

    @Override
    public void addAction(Consumer<Long> action, long periodicity) {
        timestepActorAction.put(action, new Timekeeper(periodicity));
    }

    @Override
    public Map<Consumer<Long>, Timekeeper> getTimedActions() {
        return timestepActorAction;
    }

    public void tick(long timeDiffMillis) {
        universe.tick(timeDiffMillis);
        TimeStepActionActor.super.tick(timeDiffMillis); // Check if we need to render
    }
}
