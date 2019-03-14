package fr.rochet.PlaygroundObjects;

public class Frame {

    private int x;
    private int y;

    public Frame(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isAtSamePosition(Frame otherFrame) {
        return this.x == otherFrame.x && this.y == otherFrame.y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
