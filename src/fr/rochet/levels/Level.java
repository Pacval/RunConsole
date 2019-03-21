package fr.rochet.levels;

import fr.rochet.utils.RunGameException;
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
        this.visionDistance = 4; // TODO : à voir comment géré plus tard. Dans fichier niveau ?
        this.map = null;
    }

    private void selectLevel() {
        // choix dificulté
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

        // choix niveau
        int levelNumber = 0;
        while (levelNumber == 0) {
            try {
                System.out.print("Numéro niveau : ");
                levelNumber = Integer.parseInt(br.readLine().toUpperCase());
            } catch (IOException | NumberFormatException e) {
                levelNumber = 0;
            }
        }
        this.number = levelNumber;
    }

    public String[][] getMap() throws RunGameException {
        if (map == null) {
            this.loadMap();
        }
        return map;
    }

    private void loadMap() throws RunGameException {
        try {
            File levelFile = new File("src/fr/rochet/resources/levels/" + this.difficulty.name().toLowerCase() + "/" + number + ".lvl");
            String levelFileContent = FileUtils.readFileToString(levelFile, StandardCharsets.UTF_8);
            map = Arrays.stream(levelFileContent.split("\r\n")).map(x -> x.split("")).toArray(String[][]::new);
        } catch (IOException e) {
            throw new RunGameException("Erreur lors du chargement du fichier niveau");
        }
    }

    public int getVisionDistance() {
        return visionDistance;
    }
}
