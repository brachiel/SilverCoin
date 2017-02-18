package ch.chrummibei.silvercoin.config;

import ch.chrummibei.silvercoin.universe.Universe;
import org.json.simple.JSONObject;

import java.io.Reader;

/**
 * Parses the Factory configuration file.
 */
public class ModFactoryParser implements FactoryConfig {
    private final JSONObject config;

    @Override
    public int getRandomisedIntSetting(String setting) {
        return Universe.getRandomInt(getFactoryConfigSettingInt(setting, "min"),
                                     getFactoryConfigSettingInt(setting, "max"));
    }

    @Override
    public double getRandomisedDoubleSetting(String setting) {
        return Universe.getRandomDouble(getFactoryConfigSettingDouble(setting, "min"),
                                        getFactoryConfigSettingDouble(setting, "max"));
    }

    public Object getFactoryConfigSetting(String setting, String arg) {
        return ((JSONObject) config.get(setting)).get(arg);
    }

    public int getFactoryConfigSettingInt(String setting, String arg) {
        return Math.toIntExact((long) getFactoryConfigSetting(setting, arg));
    }

    public double getFactoryConfigSettingDouble(String setting, String arg) {
        return (double) getFactoryConfigSetting(setting, arg);
    }

    public ModFactoryParser(Reader factoryConfigReader) {
        config = JSONConfigParser.configParse(factoryConfigReader);
    }
}
