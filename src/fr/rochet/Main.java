package fr.rochet;

import fr.rochet.levels.Level;
import fr.rochet.playgroundobjects.Playground;
import fr.rochet.utils.RunGameException;

public class Main {

    public static void main(String[] args) {

        // Chargement du jeu
        Playground playground = new Playground();

        try {

            // Sélection niveau
            Level level = new Level();

            // Lancement du jeu
            playground.initialize(level);

            playground.play();

        } catch (RunGameException e) {
            e.printStackTrace();
        }
    }
}