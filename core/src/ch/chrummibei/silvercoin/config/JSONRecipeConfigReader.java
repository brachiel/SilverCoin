package ch.chrummibei.silvercoin.config;

import ch.chrummibei.silvercoin.universe.item.Item;
import ch.chrummibei.silvercoin.universe.item.Recipe;
import ch.chrummibei.silvercoin.universe.item.RecipeBook;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;

/**
 * Created by brachiel on 20/02/2017.
 */
public class JSONRecipeConfigReader {
    private Json json = new Json();

    public JSONRecipeConfigReader(JSONItemConfigReader itemConfig) {
        json.setSerializer(Recipe.class, new Json.Serializer<Recipe>() {
            @Override
            public void write(Json json, Recipe object, Class knownType) {
                return;
            }

            @Override
            public Recipe read(Json json, JsonValue jsonData, Class type) {
                Item product = null;
                int amountPerBulk = 1;
                HashMap<Item,Integer> ingredients= new HashMap<>();
                Long buildTime = null;
                Float hickUpChance = null;

                for (JsonValue child : jsonData) {
                    switch (child.name()) {
                        case "product":
                            product = itemConfig.getItemByName(child.asString());
                            break;
                        case "amountPerBulk":
                            amountPerBulk = child.asInt();
                            break;
                        case "ingredients":
                            for (JsonValue ingredient : child) {
                                ingredients.put(itemConfig.getItemByName(ingredient.name()), ingredient.asInt());
                            }
                            break;
                        case "buildTime":
                            buildTime = child.asLong();
                            break;
                        case "hickUpChance":
                            hickUpChance = child.asFloat();
                            break;
                        default:
                            throw new RuntimeException("Error parsing Item Configuration; unknown field " + child.name());
                    }
                }

                return new Recipe(product, amountPerBulk, ingredients, buildTime, hickUpChance);
            }
        });

        //json.setElementType(RecipeBook.class, "recipes", Recipe.class);
    }

    public RecipeBook getRecipeBook(FileHandle defaultModRecipeJsonFile) {
        return json.fromJson(RecipeBook.class, defaultModRecipeJsonFile);
    }
}
