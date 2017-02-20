package ch.chrummibei.silvercoin.universe.item;

/**
 * Tradeable item
 */
public class Item {
    private String name;

    public Item() {}
    public Item(String name) {
        setName(name);
    }

    public void setName(String name) { this.name = name; }
    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }
}
