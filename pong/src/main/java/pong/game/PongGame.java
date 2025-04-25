package pong.game;

import javax.swing.JFrame;
import java.awt.Dimension;

/**
 * Main game window for the Pong game.
 */
public class PongGame extends JFrame {
    /** Game width in pixels */
    public static final int WIDTH = 800;
    
    /** Game height in pixels */
    public static final int HEIGHT = 600;
    
    /**
     * Creates and initializes the game window
     */
    public PongGame() {
        setTitle("Pong Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Create the game panel
        GamePanel gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        add(gamePanel);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}