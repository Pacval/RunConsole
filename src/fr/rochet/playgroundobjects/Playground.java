package fr.rochet.playgroundobjects;

import fr.rochet.levels.Level;
import fr.rochet.objects.Enemy;
import fr.rochet.objects.Exit;
import fr.rochet.objects.Obstacle;
import fr.rochet.objects.Player;

import javax.swing.text.MutableAttributeSet;
import java.util.ArrayList;
import java.util.List;

public class Playground {

    private int width;
    private int height;

    private int visionRange;

    private List<Player> players;
    private List<Enemy> enemies;
    private List<Obstacle> obstacles;
    private List<Exit> exits;

    //<editor-fold desc="Fonctions d'initialisation">

    public Playground() {
        this.players = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.exits = new ArrayList<>();
    }

    /**
     * On initialise le niveau avec l'objet level passé
     */
    public void initialize(Level level) {

        this.height = level.getMap().length;
        this.width = level.getMap()[0].length;

        this.visionRange = 5; // A voir plus tard comment paramétré. Constant ou variable ?

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                switch (level.getMap()[y][x]) {
                    case "#":
                        obstacles.add(new Obstacle(x, y));
                        break;
                    case "P":
                        players.add(new Player(x, y));
                        break;
                    case "E":
                        enemies.add(new Enemy(x, y));
                        break;
                    case "X":
                        exits.add(new Exit(x, y));
                        break;
                }
            }
        }
    }

    //</editor-fold>

    //<editor-fold desc="Fonctions principales de jeu">

    /**
     * Fonction principale déroulant le jeu sur le terrain
     */
    public void play() {
        int gameResult;
        while ((gameResult = isGameOver()) == 0) {
            printConsole();
            players.forEach(player -> player.move(obstacles, exits));
            enemies.forEach(enemy -> enemy.moveWithAstarAlgoAndAllVision(players, obstacles, enemies));
        }

        // Fin du jeu
        printConsole();
        if (gameResult < 0) {
            System.out.println("Vous vous êtes fait dévoré tout cru");
        } else {
            System.out.println("Bravo ! Vous avez réussi à vous échapper");
        }
    }

    /**
     * Vérifie si le jeu est terminé (gagné ou perdu)
     *
     * @return -1 si perdu, 1 si gagné, 0 sinon
     */
    private int isGameOver() {
        if (players.stream().allMatch(Player::isOut)) {
            return 1;
        }

        // Si au moins un joueur est sur la meme position que au moins un ennemi
        if (players.stream().anyMatch(player -> enemies.stream().anyMatch(enemy -> enemy.isAtSamePosition(player)))) {
            return -1;
        }

        return 0;
    }
    //</editor-fold>

    //<editor-fold desc="Fonctions d'affichage">
    /**
     * Permet d'afficher provisoirement le terrain de jeu dans la console
     * Peut etre amélioré (bordures (obstacles -> évite de sortir du terrain))
     */
    private void printConsole() {
        clearConsole();
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int finalX = i;
                int finalY = j;

                // On vérifie si 1 joueur à la vision sur le point
                if (players.stream().anyMatch(player -> Math.abs(player.getX() - finalX) + Math.abs(player.getY() - finalY) <= visionRange)) {
                    if (enemies.stream().anyMatch(x -> x.getX() == finalX && x.getY() == finalY)) {
                        System.out.print('E');
                    } else if (players.stream().anyMatch(x -> x.getX() == finalX && x.getY() == finalY)) {
                        System.out.print('P');
                    } else if (exits.stream().anyMatch(x -> x.getX() == finalX && x.getY() == finalY)) {
                        System.out.print('X');
                    } else if (obstacles.stream().anyMatch(x -> x.getX() == finalX && x.getY() == finalY)) {
                        System.out.print('#');
                    } else {
                        System.out.print(' ');
                    }
                } else {
                    System.out.print('o');
                }


            }
            System.out.println();
        }
    }

    /**
     * Vide la console
     */
    private void clearConsole() {
        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (Exception excpt) {
            for (int i = 0; i < 20; i++) {
                System.out.println();
            }
        }
    }
    //</editor-fold>
}
