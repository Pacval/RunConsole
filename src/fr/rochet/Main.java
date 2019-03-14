package fr.rochet;

public class Main {

    public static void main(String[] args) {
        Playground playground = new Playground(5, 5);

        playground.initialize();

        playground.printConsole();

        playground.play();
    }
}
