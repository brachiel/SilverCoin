package ch.chrummibei.silvercoin.gui;

import ch.chrummibei.silvercoin.universe.space.Universe;

import javax.swing.*;
import java.awt.*;

/**
 * Created by brachiel on 06/02/2017.
 */
public class SilverCoinComponent extends Canvas implements Runnable {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 200;
    private static final int SCALE = 4;
    private static final double TARGET_TPS = 10.0;

    private Universe universe;

    private JFrame jframe;
    private Bitmap bitmap;
    private boolean running = false;
    private Thread thread;

    public SilverCoinComponent() {
        Dimension size = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        bitmap = new Bitmap(WIDTH, HEIGHT);

        universe = new Universe();
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

            tick((nowMillis - lastTickMillis)*1000); // This might take a while
            render();

            lastTickMillis = System.currentTimeMillis();
            // We have to sleep currentTime + 1000/targetTicksPerSecond - now
            try {
                long millisToSleep = (long) (nowMillis + 1000/TARGET_TPS - lastTickMillis);
                System.out.println("Sleeping " + millisToSleep);
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

    private void render() {
        universe.printStatus();
    }

    private void tick(double timeDiff) {
        universe.tick(timeDiff);
    }
}
