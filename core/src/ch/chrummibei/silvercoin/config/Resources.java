package ch.chrummibei.silvercoin.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.*;

/**
 * Central resource handling. Contains paths as constants and converts files to Streams or Readers.
 */
public class Resources {
    private static final String basePath = "./";

    public static InputStream getStreamFrom(String resource) {
        FileHandle handle = Gdx.files.internal(basePath + resource);
        return handle.read();
    }

    public static Reader getReaderFrom(String resource) {
        FileHandle handle = Gdx.files.internal(basePath + resource);
        return handle.reader();
    }

    public static InputStream getDefaultFontStream() {
        return getStreamFrom("fonts/fixed_01.png");
    }

    public static Reader getDefaultModItemConfigReader() {
        return getReaderFrom("mods/items.json");
    }
    public static Reader getDefaultModFactoryConfigReader() {
        return getReaderFrom("mods/factories.json");
    }
}
