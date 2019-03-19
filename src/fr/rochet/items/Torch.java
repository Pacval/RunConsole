package fr.rochet.items;

public class Torch extends Item {

    private int lightRange;

    public Torch(int x, int y, int lightRange) {
        super(x, y, ItemType.TORCH);
        this.lightRange = lightRange;
    }

    public int getLightRange() {
        return lightRange;
    }
}
