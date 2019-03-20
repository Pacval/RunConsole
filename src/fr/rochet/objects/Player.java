package fr.rochet.objects;

import fr.rochet.items.Inventory;
import fr.rochet.items.ItemType;
import fr.rochet.items.Torch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Player extends GameElement {

    private boolean isOut;
    private Inventory inventory;
    private int visionRange;

    public Player(int x, int y, int visionRange) {
        super(x, y, ElementType.PLAYER);
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
        if (torches.stream().anyMatch(pos -> pos.getX() == this.getX() && pos.getY() == this.getY())){
            possibleMoves.add("P"); // PICK UP // TODO : besoin d'un bouton que pour ramasser torches. Autres items se ramasseront en marchant dessus
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
                break;
            case "T":
                if (inventory.useItem(ItemType.TORCH)) {
                    torches.add(new Torch(this.getX(), this.getY()));
                }
                break;
            case "P":
                Optional<Torch> torchToPickUp = torches.stream().filter(torch -> torch.getX() == this.getX() && torch.getY() == this.getY()).findFirst();
                if (torchToPickUp.isPresent()) {
                    torches.remove(torchToPickUp.get());
                    this.getInventory().addItem(ItemType.TORCH);
                }
                break;
            case "U":
                this.moveUp();
                break;
            case "D":
                this.moveDown();
                break;
            case "R":
                this.moveRight();
                break;
            case "L":
                this.moveLeft();
                break;
        }

        // Vérification si joueur sur sortie
        if (exits.stream().anyMatch(this::isAtSamePosition)) {
            this.isOut = true;
        }
    }

    //</editor-fold>

    //<editor-fold desc="Getters/Setters">

    public boolean isOut() {
        return isOut;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean carryTorch() {
        return inventory.getItems().get(ItemType.TORCH) > 0;
    }

    public int getVisionRange() {
        return visionRange;
    }

    //</editor-fold>
}
