package ch.chrummibei.silvercoin.config;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

/**
 * Parses the Factory configuration file.
 */
public class JSONFactoryConfigReader {
    private Json json = new Json();

    public FactoryConfig getFactoryConfig(FileHandle jsonFactoryFile) {
        return json.fromJson(FactoryConfig.class, jsonFactoryFile);
    }
}
