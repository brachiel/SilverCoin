package ch.chrummibei.silvercoin.gui;

import ch.chrummibei.silvercoin.universe.actor.TimestepActionActor;
import ch.chrummibei.silvercoin.universe.space.Universe;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Main GUI component of SilverCoin
 */
public class SilverCoinComponent extends Canvas implements Runnable, TimestepActionActor {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 200;
    private static final int SCALE = 4;
    private static final double TARGET_TPS = 10.0;

    private Universe universe;

    private JFrame jframe;
    private Bitmap bitmap;
    private boolean running = false;
    private Thread thread;
    private Map<Consumer<Long>, Timekeeper> timestepActorAction = new HashMap<>();

    public SilverCoinComponent() {
        Dimension size = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        bitmap = new Bitmap(WIDTH, HEIGHT);

        universe = new Universe();

        // Setup rendering every 1000 milliseconds
        this.addAction(this::render, 1000);
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
            // render(); // Render is solved through an ActionActor

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

    private void render(long timeDiffMillis) {
        universe.printStatus();

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
        TimestepActionActor.super.tick(timeDiffMillis); // Check if we need to render
    }
}
