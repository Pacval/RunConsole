package fr.rochet.enemies;

import fr.rochet.objects.Obstacle;
import fr.rochet.objects.Player;

import java.util.List;

/**
 * Ennemi stupide : ne bouge pas si pas de vision sur joueur.
 * Si vision sur joueur, se déplace directement vers lui.
 * <p>
 * TODO : si 2 zombies sur meme case, un tue l'autre
 */
public class Zombie extends Enemy {

    private static final int VISION_RANGE = 5;

    public Zombie(int x, int y) {
        super(x, y, EnemyType.ZOMBIE, VISION_RANGE);
    }

    @Override
    public void move(List<Player> players, List<Obstacle> obstacles, List<Enemy> enemies) {
        this.moveStraightToClosestPlayerAndRestrictedCircleVision(players, obstacles, enemies);
    }
}
