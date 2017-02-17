package ch.chrummibei.silvercoin.config;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;

/**
 * Simply parses a JSON file and returns a JSON Object. Might be expanded in the future to do more.
 */
public class JSONConfigParser {

    public static JSONObject configParse(Reader jsonConfigReader) {
        JSONParser parser = new JSONParser();
        try {
            return (JSONObject) parser.parse(jsonConfigReader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
