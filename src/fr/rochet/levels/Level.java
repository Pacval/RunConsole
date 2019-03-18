package fr.rochet.levels;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Level {

    private LevelDifficulty difficulty;
    private int number;
    private int visionDistance;
    private String[][] map;

    public Level() {
        this.selectLevel();
        this.visionDistance = 5; // TODO : à voir comment géré plus tard. Dans fichier niveau ?
        this.map = null;
    }

    private void selectLevel() { // TODO : choix dans liste, pas choix à écrire
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String difficulty = "";
        while (!(difficulty.equals("E") || difficulty.equals("M") || difficulty.equals("H"))) {
            try {
                System.out.print("Difficulty (E / M / H) : ");
                difficulty = br.readLine().toUpperCase();
            } catch (IOException e) {
                difficulty = "";
            }
        }

        switch (difficulty) {
            case "E": this.difficulty = LevelDifficulty.EASY; break;
            case "M": this.difficulty = LevelDifficulty.MEDIUM; break;
            case "H": this.difficulty = LevelDifficulty.HARD; break;
        }

        this.number = 2; // TODO : demander le numéro de niveau
    }

    public String[][] getMap() {
        if (map == null) {
            this.loadMap();
        }
        return map;
    }

    private void loadMap() {
        try {
            File levelFile = new File("src/fr/rochet/resources/levels/" + this.difficulty.name().toLowerCase() + "/" + number + ".lvl");
            String levelFileContent = FileUtils.readFileToString(levelFile, StandardCharsets.UTF_8);
            map = Arrays.stream(levelFileContent.split("\r\n")).map(x -> x.split("")).toArray(String[][]::new);
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement du niveau");
        }
    }

    public int getVisionDistance() {
        return visionDistance;
    }
}
