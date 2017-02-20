package ch.chrummibei.silvercoin.config;

import ch.chrummibei.silvercoin.universe.item.Catalogue;
import ch.chrummibei.silvercoin.universe.item.Item;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

/**
 * Created by brachiel on 20/02/2017.
 */
public class JSONItemConfigReader {
    private Json json = new Json();
    Catalogue catalogue;

    public Item getItemByName(String name) {
        return catalogue.getItems().stream().filter(item -> item.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item " + name + " not found."));
    }

    public JSONItemConfigReader() {
        super();
        //json.setElementType(Catalogue.class, "items", Item.class);
    }



    public Catalogue getCatalogue(FileHandle jsonItemFile) {
        catalogue = json.fromJson(Catalogue.class, jsonItemFile);
        return catalogue;
    }
}
