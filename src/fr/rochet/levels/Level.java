package fr.rochet.levels;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Level {

    private LevelDifficulty difficulty;
    private int number;
    private String[][] map;

    public Level(LevelDifficulty difficulty, int number) {
        this.difficulty = difficulty;
        this.number = number;
        this.map = null;
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
}
