package pong.game;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

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
        
        // Set window icons
        setIcons();
        
        // Create the game panel
        GamePanel gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        add(gamePanel);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    /**
     * Sets the application icons
     */
    private void setIcons() {
        try {
            // Define available icon sizes
            int[] iconSizes = {16, 32, 48, 64, 128};
            java.util.List<Image> icons = new java.util.ArrayList<>();
            
            // Load each available icon
            for (int size : iconSizes) {
                URL iconURL = getClass().getResource("/pong-" + size + ".png");
                if (iconURL != null) {
                    icons.add(Toolkit.getDefaultToolkit().getImage(iconURL));
                }
            }
            
            // Apply icons if any were loaded
            if (!icons.isEmpty()) {
                setIconImages(icons);
                System.out.println("Iconos cargados: " + icons.size() + " tamaños disponibles");
            } else {
                System.out.println("No se pudieron encontrar los iconos");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar los iconos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}