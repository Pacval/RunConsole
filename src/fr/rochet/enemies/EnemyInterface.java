package fr.rochet.enemies;

import fr.rochet.objects.Obstacle;
import fr.rochet.objects.Player;
import fr.rochet.utils.RunGameException;

import java.util.List;

public interface EnemyInterface {

    /**
     * Fonction de déplacement des ennemis. Les différentes classes d'ennemis font ensuite appel aux différentes méthodes de déplacement
     * présentes dans la classe abstraite Enemy
     *
     * @param players   liste des joueurs
     * @param obstacles liste des obstacles
     * @param enemies   liste des ennemis
     * @throws RunGameException exception du jeu
     */
    void move(List<Player> players, List<Obstacle> obstacles, List<Enemy> enemies) throws RunGameException;
}
