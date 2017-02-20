package ch.chrummibei.silvercoin.config;

import ch.chrummibei.silvercoin.universe.item.Catalogue;
import ch.chrummibei.silvercoin.universe.item.RecipeBook;

/**
 * Glue between the Universe and the configuration files. Everything that is configurable through files goes
 * through here.
 */
public class UniverseConfig {
    Catalogue catalogue;
    RecipeBook recipeBook;
    FactoryConfig factoryConfig;

    final JSONItemConfigReader itemConfigReader;
    final JSONRecipeConfigReader recipeConfigReader;
    final JSONFactoryConfigReader factoryConfigReader;

    public UniverseConfig() {
        itemConfigReader = new JSONItemConfigReader();
        recipeConfigReader = new JSONRecipeConfigReader(itemConfigReader);
        factoryConfigReader = new JSONFactoryConfigReader();

        catalogue = itemConfigReader.getCatalogue(Resources.getDefaultModItemJsonFile());
        recipeBook = recipeConfigReader.getRecipeBook(Resources.getDefaultModRecipeJsonFile());
        factoryConfig = factoryConfigReader.getFactoryConfig(Resources.getDefaultModFactoryJsonFile());
    }

    public FactoryConfig factory() {
        return factoryConfig;
    }
    public Catalogue catalogue() {
        return catalogue;
    }
    public RecipeBook recipeBook() {
        return recipeBook;
    }
}
