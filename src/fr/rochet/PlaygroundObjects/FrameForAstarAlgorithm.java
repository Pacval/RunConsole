package fr.rochet.PlaygroundObjects;

import java.util.Objects;

public class FrameForAstarAlgorithm extends Frame{

    private int distFromStart;
    private int distFromEnd;

    public FrameForAstarAlgorithm(int x, int y, int distFromStart, int distFromEnd) {
        super(x, y);
        this.distFromStart = distFromStart;
        this.distFromEnd = distFromEnd;
    }

    public int getTotalDistance() {
        return distFromStart + distFromEnd;
    }

    public int getDistFromStart() {
        return distFromStart;
    }

    public int getDistFromEnd() {
        return distFromEnd;
    }
}
