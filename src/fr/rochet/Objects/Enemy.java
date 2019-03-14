package fr.rochet.Objects;

import java.util.List;

public class Enemy extends GameElement{

    public Enemy(int x, int y) {
        super(x, y, ElementType.ENEMY);
    }

    public void move(List<Player> players, List<Obstacle> obstacles) {

    }
}
