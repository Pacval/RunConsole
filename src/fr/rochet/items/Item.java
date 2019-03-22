package fr.rochet.items;

import fr.rochet.objects.GameElement;

public class Item extends GameElement {

    private ItemType itemType;

    public Item(int x, int y, ItemType itemType) {
        super(x, y);
    }
}
