package ch.chrummibei.silvercoin.config;

import java.io.*;

/**
 * Created by brachiel on 15/02/2017.
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

    public static Reader getDefaultModItemJsonReader() {
        return getReaderFrom("mods/items.json");
    }
}
