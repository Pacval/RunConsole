package fr.rochet.items;

public class Torch extends Item {

    public static int LIGHT_RANGE = 5;

    public Torch(int x, int y) {
        super(x, y, ItemType.TORCH);
    }
}
