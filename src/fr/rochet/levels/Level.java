package fr.rochet.levels;

import fr.rochet.enemies.Enemy;
import fr.rochet.enemies.Werewolf;
import fr.rochet.items.Item;
import fr.rochet.items.ItemType;
import fr.rochet.items.Torch;
import fr.rochet.objects.Exit;
import fr.rochet.objects.Obstacle;
import fr.rochet.objects.Player;
import fr.rochet.utils.RunGameException;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Level {

    private LevelDifficulty difficulty;
    private int number;
    private int width;
    private int height;

    // Objets pour le playgroung
    private List<Player> players;
    private List<Enemy> enemies;
    private List<Obstacle> obstacles;
    private List<Exit> exits;
    private List<Torch> torches;
    private List<Item> items;

    public Level() throws RunGameException {
        this.selectLevel();
    }

    //<editor-fold desc="Choix du niveau">

    /**
     * Choix du niveau
     *
     * @throws RunGameException erreur lors de la récupération du niveau
     */
    private void selectLevel() throws RunGameException {
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
            case "E":
                this.difficulty = LevelDifficulty.EASY;
                break;
            case "M":
                this.difficulty = LevelDifficulty.MEDIUM;
                break;
            case "H":
                this.difficulty = LevelDifficulty.HARD;
                break;
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

        this.loadJsonFileLevel();
    }

    //</editor-fold>

    //<editor-fold desc="Chargement du niveau">

    /**
     * charge le fichier niveau JSON
     *
     * @throws RunGameException erreur lors de la lecture du fichier
     */
    private void loadJsonFileLevel() throws RunGameException {
        String levelFileContent;
        try {
            File levelFile = new File("src/fr/rochet/resources/levels/" + this.difficulty.name().toLowerCase() + "/" + number + ".json");
            levelFileContent = FileUtils.readFileToString(levelFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RunGameException("Erreur lors de la récupération du fichier niveau", e);
        }

        try {
            this.loadGameElements(levelFileContent);
        } catch (JSONException e) {
            throw new RunGameException("Erreur lors de la lecture du fichier niveau", e);
        }
    }

    /**
     * charge les différents éléments du niveau
     *
     * @param levelFileContent contenu du fichier niveau
     */
    private void loadGameElements(String levelFileContent) throws JSONException {
        JSONObject root = new JSONObject(levelFileContent);

        this.width = root.getInt("width");
        this.height = root.getInt("height");

        /* JOUEURS */
        this.players = new ArrayList<>();
        JSONArray jsonPlayers = root.getJSONArray("players");
        for (int i = 0; i < jsonPlayers.length(); i++) {
            JSONObject jsonPlayer = (JSONObject) jsonPlayers.get(i);

            Player player = new Player(
                    jsonPlayer.getInt("x"),
                    jsonPlayer.getInt("y"),
                    jsonPlayer.getInt("visionRange"));

            JSONArray jsonItems = ((JSONObject) jsonPlayer.get("inventory")).getJSONArray("items");
            for (int j = 0; j < jsonItems.length(); j++) {
                player.getInventory().addItem(ItemType.valueOf(((JSONObject) jsonItems.get(i)).getString("type")), ((JSONObject) jsonItems.get(i)).getInt("number"));
            }

            this.players.add(player);
        }

        /* ENNEMIS */
        this.enemies = new ArrayList<>();
        JSONArray jsonEnemies = root.getJSONArray("enemies");
        for (int i = 0; i < jsonEnemies.length(); i++) {
            JSONObject jsonEnemy = (JSONObject) jsonEnemies.get(i);
            switch (jsonEnemy.getString("enemyType")) {
                case "WEREWOLF":
                    this.enemies.add(new Werewolf(jsonEnemy.getInt("x"), jsonEnemy.getInt("y")));
                    break;
            }
        }

        /* EXITS */
        this.exits = new ArrayList<>();
        JSONArray jsonExits = root.getJSONArray("exits");
        for (int i = 0; i < jsonExits.length(); i++) {
            JSONObject jsonExit = (JSONObject) jsonExits.get(i);
            this.exits.add(new Exit(jsonExit.getInt("x"), jsonExit.getInt("y")));
        }

        /* OBSTACLES */
        this.obstacles = new ArrayList<>();
        JSONArray jsonObstacles = root.getJSONArray("obstacles");
        for (int i = 0; i < jsonObstacles.length(); i++) {
            JSONObject jsonObstacle = (JSONObject) jsonObstacles.get(i);
            this.obstacles.add(new Obstacle(jsonObstacle.getInt("x"), jsonObstacle.getInt("y")));
        }
        this.fillBorders();

        /* TORCHES */
        this.torches = new ArrayList<>();
        JSONArray jsonTorches = root.getJSONArray("torches");
        for (int i = 0; i < jsonTorches.length(); i++) {
            JSONObject jsonTorch = (JSONObject) jsonTorches.get(i);
            this.torches.add(new Torch(jsonTorch.getInt("x"), jsonTorch.getInt("y")));
        }
    }

    /**
     * Cette fonction remplit les bordures du niveaux avec des obstacles, sauf sur les sorties
     */
    private void fillBorders() {
        for (int i = 0; i < this.width; i++) {
            Obstacle obstacleUp = new Obstacle(i, 0);
            if (exits.stream().noneMatch(exit -> exit.isAtSamePosition(obstacleUp))) {
                obstacles.add(obstacleUp);
            }

            Obstacle obstacleDown = new Obstacle(i, this.height - 1);
            if (exits.stream().noneMatch(exit -> exit.isAtSamePosition(obstacleDown))) {
                obstacles.add(obstacleDown);
            }
        }
        for (int i = 1; i < this.height - 1; i++) {
            Obstacle obstacleLeft = new Obstacle(0, i);
            if (exits.stream().noneMatch(exit -> exit.isAtSamePosition(obstacleLeft))) {
                obstacles.add(obstacleLeft);
            }

            Obstacle obstacleRight = new Obstacle(this.width - 1, i);
            if (exits.stream().noneMatch(exit -> exit.isAtSamePosition(obstacleRight))) {
                obstacles.add(obstacleRight);
            }
        }
    }

    //</editor-fold>

    //<editor-fold desc="Getters / Setters">

    public LevelDifficulty getDifficulty() {
        return difficulty;
    }

    public int getNumber() {
        return number;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public List<Exit> getExits() {
        return exits;
    }

    public List<Torch> getTorches() {
        return torches;
    }

    public List<Item> getItems() {
        return items;
    }

    //</editor-fold>
}
