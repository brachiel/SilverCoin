package ch.chrummibei.silvercoin.config;

import java.io.*;

/**
 * Central resource handling. Contains paths as constants and converts files to Streams or Readers.
 */
public class Resources {
    private static final String basePath = "resources/";

    public static InputStream getStreamFrom(String resource) {
        try {
            return new FileInputStream(basePath + resource);
        } catch (FileNotFoundException e) {
            System.out.println("Resource not found: " + basePath + resource);
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public static Reader getReaderFrom(String resource) {
        try {
            return new FileReader(basePath + resource);
        } catch (FileNotFoundException e) {
            System.out.println("Resource not found: " + basePath + resource);
            e.printStackTrace();
            System.exit(1);
        }
        return null;
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
