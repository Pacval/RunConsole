package fr.rochet.objects;

import fr.rochet.playgroundobjects.Frame;

public abstract class GameElement extends Frame {

    public GameElement(int x, int y) {
        super(x, y);
    }

    protected void moveUp() {
        this.setY(this.getY() - 1);
    }

    protected void moveDown() {
        this.setY(this.getY() + 1);
    }

    protected void moveRight() {
        this.setX(this.getX() + 1);
    }

    protected void moveLeft() {
        this.setX(this.getX() - 1);
    }
}
