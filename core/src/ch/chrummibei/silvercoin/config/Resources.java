package ch.chrummibei.silvercoin.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.io.*;

/**
 * Central resource handling. Contains paths as constants and converts files to Streams or Readers.
 */
public class Resources {
    private static final String basePath = "./";

    public static FileHandle getFileHandle(String resource) {
        return Gdx.files.internal(basePath + resource);
    }
    public static InputStream getStreamFrom(String resource) {
        return getFileHandle(resource).read();
    }

    public static Reader getReaderFrom(String resource) {
        return getFileHandle(resource).reader();
    }

    public static BitmapFont getDefaultFont() {
        return new BitmapFont(getFileHandle("fonts/fixed_font.fnt"), false);
    }

    public static Reader getDefaultModItemConfigReader() {
        return getReaderFrom("mods/items.json");
    }
    public static Reader getDefaultModFactoryConfigReader() {
        return getReaderFrom("mods/factories.json");
    }
}
