package fr.rochet.objects;

import fr.rochet.playgroundobjects.FrameForAstarAlgorithm;

import java.util.*;

public class Enemy extends GameElement {

    public Enemy(int x, int y) {
        super(x, y, ElementType.ENEMY);
    }

    //<editor-fold desc="Fonctions de mouvement">
    /**
     * On part du principe que l'ennemi a vision sur tout le terrain, comme le joueur. Il va donc utiliser l'algorithme A* pour aller le plus vite vers le joueur
     *
     * @param players   liste des joueurs
     * @param obstacles liste des obstacles
     */
    public void moveWithAstarAlgoAndAllVision(List<Player> players, List<Obstacle> obstacles, List<Enemy> enemies) {
        Player closestPlayer = getClosestPlayer(players);

        if (closestPlayer != null) {
            if (Math.abs(closestPlayer.getX() - this.getX()) + Math.abs(closestPlayer.getY() - this.getY()) == 0) {
                // Un joueur est sur la position de l'ennemi. Pas besoin de le bouger
                return;
            }

            Set<FrameForAstarAlgorithm> possiblePath = new HashSet<>();
            Set<FrameForAstarAlgorithm> testedPath = new HashSet<>();

            // On part du joueur et on va jusqu'à l'ennemi. C'est plus simple pour récupérer le résultat (direction suivante)
            FrameForAstarAlgorithm currentFrame = new FrameForAstarAlgorithm(closestPlayer.getX(), closestPlayer.getY(), 0, Math.abs(this.getX() - closestPlayer.getX()) + Math.abs(this.getY() - closestPlayer.getY()));
            possiblePath.add(currentFrame);

            boolean found = false;
            while (!possiblePath.isEmpty() && !found) {
                currentFrame = possiblePath.stream().min(Comparator.comparing(FrameForAstarAlgorithm::getTotalDistance)).get();
                possiblePath.remove(currentFrame);

                for (FrameForAstarAlgorithm adjFrame : getAdjacentFrames(currentFrame)) {

                    // On teste si le suivant est notre ennemi -> si oui on a trouvé le chemin, on doit le remonter de 1 donc aller sur la case qu'on teste actuellement
                    if(adjFrame.isAtSamePosition(this)) {
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
    }

    private Player getClosestPlayer(List<Player> players) {
        return players.stream().min(Comparator.comparing(x -> Math.abs(x.getX() - this.getX()) + Math.abs(x.getY() - this.getY()))).orElse(null);
    }

    private List<FrameForAstarAlgorithm> getAdjacentFrames(FrameForAstarAlgorithm frame) {
        List<FrameForAstarAlgorithm> adj = new ArrayList<>();
        adj.add(new FrameForAstarAlgorithm(frame.getX() + 1, frame.getY(), frame.getDistFromStart() + 1, Math.abs(frame.getX() + 1 - this.getX()) + Math.abs(frame.getY() - this.getY()))); // droite
        adj.add(new FrameForAstarAlgorithm(frame.getX() - 1, frame.getY(), frame.getDistFromStart() + 1, Math.abs(frame.getX() - 1 - this.getX()) + Math.abs(frame.getY() - this.getY()))); // gauche
        adj.add(new FrameForAstarAlgorithm(frame.getX(), frame.getY() + 1, frame.getDistFromStart() + 1, Math.abs(frame.getX() - this.getX()) + Math.abs(frame.getY() + 1 - this.getY()))); // bas
        adj.add(new FrameForAstarAlgorithm(frame.getX(), frame.getY() - 1, frame.getDistFromStart() + 1, Math.abs(frame.getX() - this.getX()) + Math.abs(frame.getY() - 1 - this.getY()))); // haut

        return adj;
    }

    private void moveToward(FrameForAstarAlgorithm frame) {
        if (frame.getX() == this.getX()) {
            if(frame.getY() < this.getY()) {
                this.moveUp();
            } else if (frame.getY() > this.getY()) {
                this.moveDown();
            }
        } else if (frame.getY() == this.getY()) {
            if(frame.getX() < this.getX()) {
                this.moveLeft();
            } else if (frame.getX() > this.getX()) {
                this.moveRight();
            }
        }
    }
    //</editor-fold>
}
