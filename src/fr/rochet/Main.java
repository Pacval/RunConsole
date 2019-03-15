package fr.rochet;

import fr.rochet.levels.Level;
import fr.rochet.levels.LevelDifficulty;
import fr.rochet.playgroundobjects.Playground;

public class Main {

    public static void main(String[] args) {
        // Chargement du jeu
        Playground playground = new Playground();

        // Sélection niveau
        Level level = new Level(LevelDifficulty.EASY, 1);

        // Lancement du jeu
        playground.initialize(level);

        playground.play();
    }
}
