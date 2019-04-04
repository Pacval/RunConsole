package fr.rochet.objects;

import fr.rochet.items.Inventory;
import fr.rochet.items.Item;
import fr.rochet.items.ItemType;
import fr.rochet.items.Torch;
import fr.rochet.playgroundobjects.Frame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Player extends GameElement {

    private boolean isOut;
    private Inventory inventory;
    private int visionRange;

    public Player(int x, int y, int visionRange) {
        super(x, y);
        this.isOut = false;
        this.inventory = new Inventory();
        this.visionRange = visionRange;
    }

    //<editor-fold desc="Fonctions de mouvement">

    public void move(List<Obstacle> obstacles, List<Exit> exits, List<Torch> torches) {

        // On check les mouvements possibles en fonction des obstacles
        List<String> possibleMoves = new ArrayList<>();
        possibleMoves.add("S"); // STAY
        if (this.carryTorch() && torches.stream().noneMatch(pos -> pos.getX() == this.getX() && pos.getY() == this.getY())) {
            possibleMoves.add("T"); // TORCH
        }
        if (this.carryAxe() && !this.getDestructibleObstaclesNearPlayer(obstacles).isEmpty()) {
            possibleMoves.add("A"); // AXE
        }
        if (torches.stream().anyMatch(pos -> pos.getX() == this.getX() && pos.getY() == this.getY())) {
            possibleMoves.add("P"); // PICK UP (TORCH)
        }
        if (obstacles.stream().noneMatch(pos -> pos.getX() == this.getX() && pos.getY() == this.getY() - 1)) {
            possibleMoves.add("U"); // UP
        }
        if (obstacles.stream().noneMatch(pos -> pos.getX() == this.getX() && pos.getY() == this.getY() + 1)) {
            possibleMoves.add("D"); // DOWN
        }
        if (obstacles.stream().noneMatch(pos -> pos.getX() == this.getX() - 1 && pos.getY() == this.getY())) {
            possibleMoves.add("L"); // LEFT
        }
        if (obstacles.stream().noneMatch(pos -> pos.getX() == this.getX() + 1 && pos.getY() == this.getY())) {
            possibleMoves.add("R"); // RIGHT
        }

        boolean actionDone = false;
        while (!actionDone) {
            // Récupération entrée joueur
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String move = "";
            while (!possibleMoves.contains(move.toUpperCase())) {
                try {
                    System.out.print("Where do you move ? " + possibleMoves.toString() + " : ");
                    move = br.readLine();
                } catch (IOException e) {
                    move = "";
                }
            }

            // Mouvement
            switch (move.toUpperCase()) {
                case "S":
                    actionDone = true;
                    break;
                case "T":
                    if (inventory.useItem(ItemType.TORCH)) {
                        torches.add(new Torch(this.getX(), this.getY()));
                    }
                    actionDone = true;
                    break;
                case "P":
                    Optional<Torch> torchToPickUp = torches.stream().filter(torch -> torch.getX() == this.getX() && torch.getY() == this.getY()).findFirst();
                    if (torchToPickUp.isPresent()) {
                        torches.remove(torchToPickUp.get());
                        this.inventory.addItem(ItemType.TORCH);
                    }
                    actionDone = true;
                    break;
                case "A":
                    actionDone = this.askAndDestructObstacle(obstacles);
                    break;
                case "U":
                    this.moveUp();
                    actionDone = true;
                    break;
                case "D":
                    this.moveDown();
                    actionDone = true;
                    break;
                case "R":
                    this.moveRight();
                    actionDone = true;
                    break;
                case "L":
                    this.moveLeft();
                    actionDone = true;
                    break;
            }
        }

        // Vérification si joueur sur sortie
        if (exits.stream().anyMatch(this::isAtSamePosition)) {
            this.isOut = true;
        }
    }

    //</editor-fold>

    //<editor-fold desc="Fonctions d'inventaire">

    /**
     * Ajoute les items au sol, à la position du joueur, à son inventaire
     *
     * @param items liste des items au sol
     */
    public void pickUpItem(List<Item> items) {
        List<Item> itemsAtPosition = items.stream().filter(item -> item.isAtSamePosition(this)).collect(Collectors.toList());
        if (!itemsAtPosition.isEmpty()) {
            for (Item item : itemsAtPosition) {
                this.inventory.addItem(item.getItemType());
            }
            items.removeAll(itemsAtPosition);
        }
    }

    public void addItem(ItemType itemType, int number) {
        this.inventory.addItem(itemType, number);
    }

    /**
     * @return true si le joueur porte une torche, false sinon
     */
    public boolean carryTorch() {
        return inventory.getItems().get(ItemType.TORCH) > 0;
    }

    /**
     * @return true si le joueur porte une hache, false sinon
     */
    private boolean carryAxe() {
        return inventory.getItems().get(ItemType.AXE) > 0;
    }

    public boolean useItem(ItemType itemType) {
        return this.inventory.useItem(itemType);
    }

    public void printInventory() {
        this.inventory.printConsole();
    }

    //</editor-fold>

    //<editor-fold desc="Fonctions de manipulation du terrain">

    /**
     * @param obstacles liste des obstacles sur le terrain
     * @return la liste des obstacles destructibles à côté du joueur
     */
    private List<Obstacle> getDestructibleObstaclesNearPlayer(List<Obstacle> obstacles) {
        List<Frame> framesNearPlayer = new ArrayList<>();
        framesNearPlayer.add(new Frame(this.getX() - 1, this.getY()));
        framesNearPlayer.add(new Frame(this.getX() + 1, this.getY()));
        framesNearPlayer.add(new Frame(this.getX(), this.getY() - 1));
        framesNearPlayer.add(new Frame(this.getX(), this.getY() + 1));

        return obstacles.stream()
                .filter(obstacle -> framesNearPlayer.stream() // pour tous les obstacles
                        .anyMatch(frame -> frame.isAtSamePosition(obstacle))) // on récupère ceux qui sont a cote du joueur
                .filter(obstacle -> !obstacle.isImmovable()) // on filtre sur ceux qu'on peut bouger / détruire
                .collect(Collectors.toList());
    }

    private boolean askAndDestructObstacle(List<Obstacle> obstacles) {

        List<Obstacle> destructibleObstacles = this.getDestructibleObstaclesNearPlayer(obstacles);
        if (destructibleObstacles.isEmpty()) {
            // Normalement si on est ici il y a forcément un obstacle à détruire mais on check au cas où
            return false;
        }

        Predicate<Obstacle> left = obstacle -> obstacle.isAtSamePosition(new Frame(this.getX() - 1, this.getY()));
        Predicate<Obstacle> right = obstacle -> obstacle.isAtSamePosition(new Frame(this.getX() + 1, this.getY()));
        Predicate<Obstacle> up = obstacle -> obstacle.isAtSamePosition(new Frame(this.getX(), this.getY() - 1));
        Predicate<Obstacle> down = obstacle -> obstacle.isAtSamePosition(new Frame(this.getX(), this.getY() + 1));
        List<String> obstacleDirections = new ArrayList<>();
        obstacleDirections.add("C"); // CANCEL
        if (destructibleObstacles.stream().anyMatch(left)) {
            obstacleDirections.add("L"); // LEFT
        }
        if (destructibleObstacles.stream().anyMatch(right)) {
            obstacleDirections.add("R"); // RIGHT
        }
        if (destructibleObstacles.stream().anyMatch(up)) {
            obstacleDirections.add("U"); // UP
        }
        if (destructibleObstacles.stream().anyMatch(down)) {
            obstacleDirections.add("D"); // DOWN
        }

        if (destructibleObstacles.isEmpty()) {
            // Pareil petit test même si on a déjà un testé plus haut
            return false;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String direction = "";
        while (!obstacleDirections.contains(direction.toUpperCase())) {
            try {
                System.out.print("Which obstacle to destroy ? " + obstacleDirections.toString() + " : ");
                direction = br.readLine();
            } catch (IOException e) {
                direction = "";
            }
        }

        boolean destroyed = true;
        switch (direction.toUpperCase()) {
            case "C":
                destroyed = false;
                break;
            case "L":
                obstacles.removeIf(left);
                break;
            case "R":
                obstacles.removeIf(right);
                break;
            case "U":
                obstacles.removeIf(up);
                break;
            case "D":
                obstacles.removeIf(down);
                break;
        }

        if (destroyed) {
            this.useItem(ItemType.AXE);
        }
        return destroyed;
    }

    //</editor-fold>

    //<editor-fold desc="Getters/Setters">

    public boolean isOut() {
        return isOut;
    }

    public int getVisionRange() {
        return visionRange;
    }

    //</editor-fold>
}
