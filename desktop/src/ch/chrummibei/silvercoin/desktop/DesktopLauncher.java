package ch.chrummibei.silvercoin.desktop;

import ch.chrummibei.silvercoin.SilverCoin;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1600;
        config.height = 800;
        config.foregroundFPS = 10;
        new LwjglApplication(new SilverCoin(), config);
    }
}
