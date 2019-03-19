package fr.rochet.items;

import java.util.HashMap;
import java.util.stream.Collectors;

public class Inventory {

    private HashMap<ItemType, Integer> items;

    public Inventory() {
        this.items = new HashMap<>();

        for (ItemType type : ItemType.values()) {
            items.put(type, 0);
        }
    }

    public HashMap<ItemType, Integer> getItems() {
        return items;
    }

    public void addItem(ItemType type) {
        items.put(type, items.get(type) + 1);
    }

    public boolean useItem(ItemType type) {
        if (items.get(type) > 0) {
            items.put(type, items.get(type) - 1);
            return true;
        }
        return false;
    }

    /**
     * Affiche les items d'un joueur
     */
    public void printConsole() {
        System.out.println(items.entrySet()
                .stream()
                .map(e -> e.getKey() + " : " + e.getValue())
                .collect(Collectors.joining(" / ")));
    }
}
