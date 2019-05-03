package fr.rochet.enemies;

import fr.rochet.objects.Obstacle;
import fr.rochet.objects.Player;
import fr.rochet.utils.RunGameException;

import java.util.List;

/**
 * Ennemi classique
 * <p>
 * TODO : idée : les loups garous communiquent entre eux et si 1 loup garou trouve un joueur ils vont tous vers lui (appelle les autres pdt 1 ou 2 tours, puis rebouge ? -> permet de se cacher / de s'éloigner)
 */
public class Werewolf extends Enemy {

    private static final int VISION_RANGE = 5;

    public Werewolf(int x, int y) {
        super(x, y, EnemyType.WEREWOLF, VISION_RANGE);
    }

    /**
     * Déplacement
     */
    public void move(List<Player> players, List<Obstacle> obstacles, List<Enemy> enemies) throws RunGameException {
        this.moveWithAStarAlgoAndRestrictedCircleVision(players, obstacles, enemies);
    }

}
