package fr.rochet.objects;

import fr.rochet.items.Inventory;
import fr.rochet.items.ItemType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Player extends GameElement {

    private boolean isOut;

    private Inventory inventory;

    public Player(int x, int y) {
        super(x, y, ElementType.PLAYER);
        this.isOut = false;
        this.inventory = new Inventory();
    }

    //<editor-fold desc="Fonctions de mouvement">

    public void move(List<Obstacle> obstacles, List<Exit> exits) {

        // On check les mouvements possibles en fonction des obstacles
        List<String> possibleMoves = new ArrayList<>();
        possibleMoves.add("S"); // STAY
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

    //</editor-fold>
}
