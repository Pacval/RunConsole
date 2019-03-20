package fr.rochet.enemies;

import fr.rochet.objects.Obstacle;
import fr.rochet.objects.Player;
import fr.rochet.utils.RunGameException;

import java.util.List;

public class Werewolf extends Enemy {

    private static final int VISION_RANGE = 5;

    public Werewolf(int x, int y) {
        super(x, y, EnemyType.WEREWOLF, VISION_RANGE);
    }

    /**
     * Déplacement
     */
    public void move(List<Player> players, List<Obstacle> obstacles, List<Enemy> enemies) throws RunGameException {
        this.moveWithAstarAlgoAndRestrictedCircleVision(players, obstacles, enemies);
    }

}
