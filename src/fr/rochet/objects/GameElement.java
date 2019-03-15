package fr.rochet.objects;

import fr.rochet.playgroundobjects.Frame;

public abstract class GameElement extends Frame {

    private ElementType type;

    public GameElement(int x, int y, ElementType type) {
        super(x, y);
        this.type = type;
    }

    public void moveUp() {
        this.setY(this.getY() - 1);
    }

    public void moveDown() {
        this.setY(this.getY() + 1);
    }

    public void moveRight() {
        this.setX(this.getX() + 1);
    }

    public void moveLeft() {
        this.setX(this.getX() - 1);
    }

    public ElementType getType() {
        return type;
    }
}
