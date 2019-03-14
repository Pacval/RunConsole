package fr.rochet;

import fr.rochet.Objects.Enemy;
import fr.rochet.Objects.Exit;
import fr.rochet.Objects.Obstacle;
import fr.rochet.Objects.Player;

import java.util.ArrayList;
import java.util.List;

public class Playground {

    private int innerWidth;
    private int innerHeight;

    private int totalWidth;
    private int totalHeight;

    private List<Player> players;
    private List<Enemy> enemies;
    private List<Obstacle> obstacles;
    private List<Exit> exits;

    //<editor-fold desc="Fonctions d'initialisation">
    public Playground(int width, int height) {
        this.innerWidth = width;
        this.innerHeight = height;
        this.totalWidth = width + 2;
        this.totalHeight = height + 2;

        this.players = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.exits = new ArrayList<>();
    }

    /**
     * On pourra passer le nombre de players, d'ennemis et d'objet
     * ou alors passer la difficulté et la fonction sait à quoi ça correspond
     */
    public void initialize() {

        // sortie
        exits.add(new Exit(totalWidth - 1, totalHeight / 2));

        /* ----- LA POSITION DE TOUS LES AUTRES OBJETS DOIT ETRE ENTRE 1 ET INNERHEIGHT/WIDTH ----- */

        // joueur(s)
        players.add(new Player(1, 1));

        // ennemi(s)
        enemies.add(new Enemy(2, 4));

        // obstacle(s)
        obstacles.add(new Obstacle(2, 2));

        // initialisation bordures
        this.createBorders();
    }

    private void createBorders() {
        for (int i = 0; i < totalWidth; i++) {
            Obstacle obstacleUp = new Obstacle(i, 0);
            if (exits.stream().noneMatch(exit -> exit.isAtSamePosition(obstacleUp))) {
                obstacles.add(obstacleUp);
            }

            Obstacle obstacleDown = new Obstacle(i, totalHeight - 1);
            if (exits.stream().noneMatch(exit -> exit.isAtSamePosition(obstacleDown))) {
                obstacles.add(obstacleDown);
            }
        }
        for (int i = 1; i < totalWidth - 1; i++) {
            Obstacle obstacleLeft = new Obstacle(0, i);
            if (exits.stream().noneMatch(exit -> exit.isAtSamePosition(obstacleLeft))) {
                obstacles.add(obstacleLeft);
            }

            Obstacle obstacleRight = new Obstacle(totalWidth - 1, i);
            if (exits.stream().noneMatch(exit -> exit.isAtSamePosition(obstacleRight))) {
                obstacles.add(obstacleRight);
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
            players.forEach(player -> player.move(obstacles, exits));
            printConsole();
        }
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
    public void printConsole() {
        // clearConsole();
        for (int j = 0; j < totalHeight; j++) {
            for (int i = 0; i < totalWidth; i++) {
                int finalX = i;
                int finalY = j;
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
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    //</editor-fold>
}
