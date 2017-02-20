package ch.chrummibei.silvercoin.config;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;

/**
 * Created by brachiel on 20/02/2017.
 */
public class FactoryConfig implements Json.Serializable {
    private HashMap<String, RandomRangeConfig> options = new HashMap<>();

    public double getRandomDouble(String optionName) {
        return options.get(optionName).getRandomDouble();
    }

    public int getRandomInt(String optionName) {
        return options.get(optionName).getRandomInt();
    }

    @Override
    public void write(Json json) {
        return;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        for (JsonValue option : jsonData) {
            options.put(option.name(), json.readValue(RandomRangeConfig.class, option));
        }
    }
}
