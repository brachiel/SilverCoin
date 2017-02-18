package ch.chrummibei.silvercoin.config;

/**
 * Glue between the Universe and the configuration files. Everything that is configurable through files goes
 * through here.
 */
public class UniverseConfig {
    final ModItemParser modItemParser;
    final ModFactoryParser modFactoryParser;

    public UniverseConfig() {
        modItemParser = new ModItemParser(Resources.getDefaultModItemConfigReader());
        modFactoryParser = new ModFactoryParser(Resources.getDefaultModFactoryConfigReader());
    }

    public FactoryConfig factory() {
        return modFactoryParser;
    }

    public ItemConfig item() {
        return modItemParser;
    }
}
