package fr.rochet.Objects;

public abstract class GameElement {

    private int x;
    private int y;
    private ElementType type;

    public GameElement(int x, int y, ElementType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public boolean isAtSamePosition(GameElement otherElement) {
        return this.x == otherElement.x && this.y == otherElement.y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void moveUp() {
        this.y--;
    }

    public void moveDown() {
        this.y++;
    }

    public void moveRight() {
        this.x++;
    }

    public void moveLeft() {
        this.x--;
    }

    public ElementType getType() {
        return type;
    }
}
