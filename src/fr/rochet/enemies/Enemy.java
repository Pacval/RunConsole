package fr.rochet.enemies;

import fr.rochet.objects.GameElement;
import fr.rochet.objects.Obstacle;
import fr.rochet.objects.Player;
import fr.rochet.playgroundobjects.Frame;
import fr.rochet.playgroundobjects.FrameForAstarAlgorithm;
import fr.rochet.utils.Direction;
import fr.rochet.utils.RunGameException;

import java.util.*;

public abstract class Enemy extends GameElement implements EnemyInterface {

    private EnemyType enemyType;
    private int visionRange;

    private Frame destination;


    Enemy(int x, int y, EnemyType enemyType, int visionRange) {
        super(x, y);
        this.enemyType = enemyType;
        this.visionRange = visionRange;

        this.destination = null;

    }

    /**
     * Fonction abstraite pour forcer les classes qui 'extends' Enenmy à implémenter leur fonction 'move'
     */
    public abstract void move(List<Player> players, List<Obstacle> obstacles, List<Enemy> enemies) throws RunGameException;

    //<editor-fold desc="Fonctions des différents niveaux d'intelligence de l'AI">

    /**
     * Déplacement avec algo A*
     * Vision sur tout le terrain
     *
     * @param players   liste des joueurs
     * @param obstacles liste des obstacles
     * @param enemies   liste des ennemis
     */
    void moveWithAstarAlgoAndAllVision(List<Player> players, List<Obstacle> obstacles, List<Enemy> enemies) throws RunGameException {
        Player closestPlayer = players.stream().min(Comparator.comparing(x -> Math.abs(x.getX() - this.getX()) + Math.abs(x.getY() - this.getY()))).orElse(null);

        if (closestPlayer != null) {
            if (Math.abs(closestPlayer.getX() - this.getX()) + Math.abs(closestPlayer.getY() - this.getY()) == 0) {
                // Un joueur est sur la position de l'ennemi. Pas besoin de le bouger
                return;
            }

            this.useAStarAlgo(closestPlayer, obstacles, enemies);
        }
    }

    /**
     * Déplacement avec algo A*
     * Vision sphérique qui ignore les obstacles
     * <p>
     * Si l'ennemi a vision sur un joueur, il va vers lui.
     * Sinon, il va vers le dernier point auquel il a vu un joueur.
     * Si aucun des 2 cas n'est rempli, il choisit un point libre aléatoire du terrain et va vers celui ci
     *
     * @param players   liste des joueurs
     * @param obstacles liste des obstacles
     * @param enemies   liste des ennemis
     */
    void moveWithAStarAlgoAndRestrictedCircleVision(List<Player> players, List<Obstacle> obstacles, List<Enemy> enemies) throws RunGameException {
        Player closestPlayer = players.stream()
                .filter(player -> Math.pow(Math.abs(player.getX() - this.getX()), 2) + Math.pow(Math.abs(player.getY() - this.getY()), 2) < Math.pow(visionRange, 2))
                .min(Comparator.comparing(x -> Math.abs(x.getX() - this.getX()) + Math.abs(x.getY() - this.getY()))).orElse(null);

        if (closestPlayer != null) {
            // Si on a la vision sur un joueur, on va vers lui et on enregistre sa position
            this.useAStarAlgo(closestPlayer, obstacles, enemies);
            destination = closestPlayer;
        } else {
            // Si on a pas la vision sur un joueur, on va vers la destination enregistrée
            if (destination == null || (destination.getX() == this.getX() && destination.getY() == this.getY())) {
                // Si on a pas de destination enregistrée, ou qu'on est actuellement sur cette position, on en choisit une au hasard
                this.destination = this.selectRandomFreeFrame(obstacles);
            }
            this.useAStarAlgo(destination, obstacles, enemies);
        }
    }

    /**
     * Déplacement en ligne droite vers joueur
     * N'évite pas les obstacles. Peut donc rester bloqué
     * Vision sur tout le terrain
     *
     * @param players   liste des joueurs
     * @param obstacles liste des obstacles
     * @param enemies   liste des ennemis
     */
    void moveStraightToClosestPlayerAndAllVision(List<Player> players, List<Obstacle> obstacles, List<Enemy> enemies) {
        players.stream()
                .min(Comparator.comparing(x -> Math.abs(x.getX() - this.getX()) + Math.abs(x.getY() - this.getY())))
                .ifPresent(closestPlayer -> this.moveStraightToPlayer(closestPlayer, obstacles, enemies));

    }

    /**
     * Déplacement en ligne droite vers joueur
     * N'évite pas les obstacles. Peut donc rester bloqué
     * Vision sphérique qui ignore les obstacles
     *
     * @param players   liste des joueurs
     * @param obstacles liste des obstacles
     * @param enemies   liste des ennemis
     */
    void moveStraightToClosestPlayerAndRestrictedCircleVision(List<Player> players, List<Obstacle> obstacles, List<Enemy> enemies) {
        players.stream()
                .filter(player -> Math.pow(Math.abs(player.getX() - this.getX()), 2) + Math.pow(Math.abs(player.getY() - this.getY()), 2) < Math.pow(visionRange, 2))
                .min(Comparator.comparing(x -> Math.abs(x.getX() - this.getX()) + Math.abs(x.getY() - this.getY())))
                .ifPresent(closestPlayer -> this.moveStraightToPlayer(closestPlayer, obstacles, enemies));

    }

    //</editor-fold>

    //<editor-fold desc="Fonctions pour l'algorithme A*">

    /**
     * Déplace l'ennemi de 1 case vers la destination grâce à l'algorithme A*
     */
    private void useAStarAlgo(Frame destination, List<Obstacle> obstacles, List<Enemy> enemies) throws RunGameException {

        // On vérifie qu'on est pas déjà à la destination
        if (Math.abs(destination.getX() - this.getX()) + Math.abs(destination.getY() - this.getY()) == 0) {
            return;
        }

        Set<FrameForAstarAlgorithm> possiblePath = new HashSet<>();
        Set<FrameForAstarAlgorithm> testedPath = new HashSet<>();

        // On part du joueur et on va jusqu'à l'ennemi. C'est plus simple pour récupérer le résultat (direction suivante)
        FrameForAstarAlgorithm currentFrame = new FrameForAstarAlgorithm(destination.getX(), destination.getY(), 0, Math.abs(this.getX() - destination.getX()) + Math.abs(this.getY() - destination.getY()));
        possiblePath.add(currentFrame);

        boolean found = false;
        while (!possiblePath.isEmpty() && !found) {
            currentFrame = possiblePath.stream().min(Comparator.comparing(FrameForAstarAlgorithm::getTotalDistance)).orElseThrow(() -> new RunGameException("[Algo A*] - Erreur lors de la récupération de la frame à tester"));
            possiblePath.remove(currentFrame);

            for (FrameForAstarAlgorithm adjFrame : getAdjacentFrames(currentFrame)) {

                // On teste si le suivant est notre ennemi -> si oui on a trouvé le chemin, on doit le remonter de 1 donc aller sur la case qu'on teste actuellement
                if (adjFrame.isAtSamePosition(this)) {
                    moveToward(currentFrame);
                    found = true;
                    break;
                }
                // sinon on vérifie que la position suivante n'est pas un mur ou un autre ennemi
                else if (obstacles.stream().noneMatch(adjFrame::isAtSamePosition) && enemies.stream().noneMatch(adjFrame::isAtSamePosition)) {

                    Optional<FrameForAstarAlgorithm> possiblePoint = possiblePath.stream().filter(x -> x.getX() == adjFrame.getX() && x.getY() == adjFrame.getY()).findFirst();
                    Optional<FrameForAstarAlgorithm> testedPoint = testedPath.stream().filter(x -> x.getX() == adjFrame.getX() && x.getY() == adjFrame.getY()).findFirst();

                    if (possiblePoint.isPresent()) {
                        // si dans points possibles
                        if (possiblePoint.get().getTotalDistance() > adjFrame.getTotalDistance()) {
                            // chemin plus court, on remplace; sinon rien
                            possiblePath.remove(possiblePoint.get());
                            possiblePath.add(adjFrame);
                        }
                    } else if (testedPoint.isPresent()) {
                        // Si dans points testés
                        if (testedPoint.get().getTotalDistance() > adjFrame.getTotalDistance()) {
                            // chemin plus courte, on sort et on ajoute aux points possibles; sinon rien
                            testedPath.remove(testedPoint.get());
                            possiblePath.add(adjFrame);
                        }
                    } else {
                        // sinon on ajoute aux chemins possibles
                        possiblePath.add(adjFrame);
                    }
                }
            }
        }
    }

    private List<FrameForAstarAlgorithm> getAdjacentFrames(FrameForAstarAlgorithm frame) {
        List<FrameForAstarAlgorithm> adj = new ArrayList<>();
        adj.add(new FrameForAstarAlgorithm(frame.getX() + 1, frame.getY(), frame.getDistFromStart() + 1, Math.abs(frame.getX() + 1 - this.getX()) + Math.abs(frame.getY() - this.getY()))); // droite
        adj.add(new FrameForAstarAlgorithm(frame.getX() - 1, frame.getY(), frame.getDistFromStart() + 1, Math.abs(frame.getX() - 1 - this.getX()) + Math.abs(frame.getY() - this.getY()))); // gauche
        adj.add(new FrameForAstarAlgorithm(frame.getX(), frame.getY() + 1, frame.getDistFromStart() + 1, Math.abs(frame.getX() - this.getX()) + Math.abs(frame.getY() + 1 - this.getY()))); // bas
        adj.add(new FrameForAstarAlgorithm(frame.getX(), frame.getY() - 1, frame.getDistFromStart() + 1, Math.abs(frame.getX() - this.getX()) + Math.abs(frame.getY() - 1 - this.getY()))); // haut

        return adj;
    }

    //</editor-fold>

    //<editor-fold desc="Fonctions de déplacement direct">

    private void moveStraightToPlayer(Player closestPlayer, List<Obstacle> obstacles, List<Enemy> enemies) {
        List<Direction> directions = new ArrayList<>();
        if (closestPlayer.getY() < this.getY()) {
            directions.add(Direction.UP);
        }
        if (closestPlayer.getY() > this.getY()) {
            directions.add(Direction.DOWN);
        }
        if (closestPlayer.getX() < this.getX()) {
            directions.add(Direction.LEFT);
        }
        if (closestPlayer.getX() > this.getX()) {
            directions.add(Direction.RIGHT);
        }

        // On mélange la liste aléatoirement
        Collections.shuffle(directions);

        boolean moved = false;
        for (Direction direction : directions) {
            if (!moved) {
                // On récupère la prochaine frame de l'ennemi selon ce déplacement
                Frame nextFrame = null;
                switch (direction) {
                    case UP:
                        nextFrame = new Frame(this.getX(), this.getY() - 1);
                        break;
                    case DOWN:
                        nextFrame = new Frame(this.getX(), this.getY() + 1);
                        break;
                    case LEFT:
                        nextFrame = new Frame(this.getX() - 1, this.getY());
                        break;
                    case RIGHT:
                        nextFrame = new Frame(this.getX() + 1, this.getY());
                        break;
                }

                // On vérifie que la prochaine frame n'est pas un obstacle ou un ennemi
                Frame finalNextFrame = nextFrame;
                if (obstacles.stream().noneMatch(item -> item.isAtSamePosition(finalNextFrame))
                        && enemies.stream().noneMatch(item -> item.isAtSamePosition(finalNextFrame))) {
                    this.moveToward(finalNextFrame);
                    moved = true;
                }
            }
        }
    }

    //</editor-fold>

    //<editor-fold desc="Fonctions privées diverses">

    private void moveToward(Frame frame) {
        if (frame.getX() == this.getX()) {
            if (frame.getY() < this.getY()) {
                this.moveUp();
            } else if (frame.getY() > this.getY()) {
                this.moveDown();
            }
        } else if (frame.getY() == this.getY()) {
            if (frame.getX() < this.getX()) {
                this.moveLeft();
            } else if (frame.getX() > this.getX()) {
                this.moveRight();
            }
        }
    }

    private Frame selectRandomFreeFrame(List<Obstacle> obstacles) throws RunGameException {
        Frame destination;
        Random randomGenerator = new Random();

        do {
            destination = new Frame(
                    randomGenerator.nextInt(obstacles.stream().mapToInt(Frame::getX).max().orElseThrow(() -> new RunGameException("[Ennemi] - Erreur lors de la génération d'un nombre aléatoire (getX)"))),
                    randomGenerator.nextInt(obstacles.stream().mapToInt(Frame::getY).max().orElseThrow(() -> new RunGameException("[Ennemi] - Erreur lors de la génération d'un nombre aléatoire (getY)")))
            );
        } while (isFrameAnObstacle(destination, obstacles));

        return destination;
    }

    private boolean isFrameAnObstacle(Frame frame, List<Obstacle> obstacles) {
        return obstacles.stream().anyMatch(obstacle -> obstacle.isAtSamePosition(frame));
    }

    //</editor-fold>

    //<editor-fold desc="Getters / Setters">

    public EnemyType getEnemyType() {
        return enemyType;
    }

    //</editor-fold>
}
