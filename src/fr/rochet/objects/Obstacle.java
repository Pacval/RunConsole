package fr.rochet.objects;

public class Obstacle extends GameElement {

    private boolean immovable;

    public Obstacle(int x, int y, boolean immovable) {
        super(x, y);
        this.immovable = immovable;
    }

    public boolean isImmovable() {
        return immovable;
    }
}
