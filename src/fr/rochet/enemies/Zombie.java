package fr.rochet.enemies;

import fr.rochet.objects.Obstacle;
import fr.rochet.objects.Player;
import fr.rochet.utils.RunGameException;

import java.util.List;

/**
 * Ennemi classique mais lent
 */
public class Zombie extends Enemy {

    private static final int VISION_RANGE = 5;

    private static final int TURN_TO_MOVE = 2;

    private int turn;

    public Zombie(int x, int y) {
        super(x, y, EnemyType.ZOMBIE, VISION_RANGE);
        turn = 1;
    }

    @Override
    public void move(List<Player> players, List<Obstacle> obstacles, List<Enemy> enemies) throws RunGameException {
        if (turn == TURN_TO_MOVE) {
            this.moveWithAstarAlgoAndRestrictedCircleVision(players, obstacles, enemies);
            turn = 1;
        } else {
            turn++;
        }
    }
}
