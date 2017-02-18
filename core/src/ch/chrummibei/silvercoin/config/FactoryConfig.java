package ch.chrummibei.silvercoin.config;

/**
 * Interface to the factory configuration that a mod needs to deliver.
 */
public interface FactoryConfig {
    int getRandomisedIntSetting(String setting);
    double getRandomisedDoubleSetting(String setting);
}
