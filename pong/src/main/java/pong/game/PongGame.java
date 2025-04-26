package pong.game;

import javax.swing.JFrame;
import javax.swing.ImageIcon;
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
        
        // Set window icons for 16x16 and 32x32
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
            // Ruta a los iconos dentro del classpath (usando los nombres correctos)
            URL icon16URL = getClass().getResource("/pong-16.png");
            URL icon32URL = getClass().getResource("/pong-32.png");
            URL icon48URL = getClass().getResource("/pong-48.png");
            URL icon64URL = getClass().getResource("/pong-64.png");
            URL icon128URL = getClass().getResource("/pong-128.png");
            
            // Crear lista para todos los iconos disponibles
            java.util.List<Image> icons = new java.util.ArrayList<>();
            
            // Añadir cada icono disponible a la lista
            if (icon16URL != null) icons.add(Toolkit.getDefaultToolkit().getImage(icon16URL));
            if (icon32URL != null) icons.add(Toolkit.getDefaultToolkit().getImage(icon32URL));
            if (icon48URL != null) icons.add(Toolkit.getDefaultToolkit().getImage(icon48URL));
            if (icon64URL != null) icons.add(Toolkit.getDefaultToolkit().getImage(icon64URL));
            if (icon128URL != null) icons.add(Toolkit.getDefaultToolkit().getImage(icon128URL));
            
            // Aplicar los iconos si se encontró al menos uno
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