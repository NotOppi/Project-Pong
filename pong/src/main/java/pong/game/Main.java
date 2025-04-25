package pong.game;

import javax.swing.SwingUtilities;

/**
 * Main class that launches the Pong game.
 */
public class Main {
    /**
     * Entry point for the application
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PongGame());
    }
}