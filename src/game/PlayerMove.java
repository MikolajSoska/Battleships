package game;

import java.io.Serializable;

public class PlayerMove implements Serializable {
    private int x;
    private int y;
    private boolean sunk;

    PlayerMove(int x, int y, boolean sunk) {
        this.x = x;
        this.y = y;
        this.sunk = sunk;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isSunk() {
        return sunk;
    }
}
