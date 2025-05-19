package pong.game.view.screens;

import pong.game.controller.GameController;
import pong.game.view.ModernButton;
import pong.game.view.interfaces.InstructionsScreenInterface;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;

/**
 * Pantalla de instrucciones del juego.
 * Implementa el patrón MVC estricto, mostrando las reglas del juego
 * y controles para los modos de un jugador y multijugador.
 */
public class InstructionsScreen extends JPanel implements InstructionsScreenInterface {
    // Controlador del juego
    private GameController controller;
    
    // Componentes UI
    private ModernButton closeButton;
    
    // Variables de estado
    private Color backgroundColor = Color.BLACK;
    private Color textColor = Color.WHITE;
    private Color overlayColor = new Color(0, 0, 0, 180);
    private boolean isMultiplayerMode = false;
    
    /**
     * Constructor de la pantalla de instrucciones.
     * 
     * @param controller Controlador del juego
     */
    public InstructionsScreen(GameController controller) {
        this.controller = controller;
        controller.registerInstructionsScreen(this);
        
        setLayout(null);
        setOpaque(false);
        
        // Configurar botón de cerrar
        initCloseButton();
    }
    
    /**
     * Inicializa el botón de cerrar.
     */
    private void initCloseButton() {
        closeButton = new ModernButton("X", true);
        closeButton.setBounds(720, 20, 40, 40);
        closeButton.setFocusable(false);
        closeButton.addActionListener(_ -> {
            controller.navigateToMainMenu();
        });
        add(closeButton);
    }
    
    // Implementaciones de InstructionsScreenInterface
    @Override
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }
    
    @Override
    public void setTextColor(Color color) {
        this.textColor = color;
    }
    
    @Override
    public void setOverlayColor(Color color) {
        this.overlayColor = color;
    }
    
    @Override
    public void setMultiplayerMode(boolean isMultiplayer) {
        this.isMultiplayerMode = isMultiplayer;
    }
    
    @Override
    public void updateButtonThemes(Color buttonColor, Color buttonTextColor) {
        if (closeButton != null) {
            closeButton.setButtonColor(buttonColor);
            closeButton.setTextColor(buttonTextColor);
        }
    }
    
    @Override
    public void refresh() {
        repaint();
    }
    
    // Métodos sobrescritos
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Dibuja overlay semitransparente
        g.setColor(overlayColor);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        drawTitle(g);
        drawInstructions(g);
        drawFooter(g);
    }
    
    /**
     * Dibuja el título de la pantalla de instrucciones.
     */
    private void drawTitle(Graphics g) {
        g.setColor(textColor);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        String title = "Cómo Jugar";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, getWidth() / 2 - titleWidth / 2, 80);
    }
    
    /**
     * Dibuja las instrucciones según el modo de juego.
     */
    private void drawInstructions(Graphics g) {
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String[] instructions;
        
        if (isMultiplayerMode) {
            instructions = new String[] {
                "• Jugador 1: Usa la tecla W para mover la paleta hacia arriba",
                "• Jugador 1: Usa la tecla S para mover la paleta hacia abajo",
                "• Jugador 2: Usa la FLECHA ARRIBA para mover la paleta hacia arriba", 
                "• Jugador 2: Usa la FLECHA ABAJO para mover la paleta hacia abajo",
                "• Anota puntos haciendo que la pelota pase la paleta del oponente",
                "• Presiona ESPACIO para pausar/reanudar el juego",
                "• El primer jugador en llegar a 10 puntos gana!"
            };
        } else {
            instructions = new String[] {
                "• Usa la tecla W para mover la paleta hacia arriba",
                "• Usa la tecla S para mover la paleta hacia abajo",
                "• Anota puntos haciendo que la pelota pase la paleta de la IA",
                "• Presiona ESPACIO para pausar/reanudar el juego",
                "• El primer jugador en llegar a 10 puntos gana!"
            };
        }
        
        // Calcular posición inicial para centrar verticalmente las instrucciones
        int totalHeight = instructions.length * 40; // 40px por línea
        int startY = (getHeight() - totalHeight) / 2 + 20; // +20 para compensar por el título
        
        // Dibujar instrucciones centradas horizontalmente
        int maxWidth = 0;
        for (String line : instructions) {
            int lineWidth = g.getFontMetrics().stringWidth(line);
            if (lineWidth > maxWidth) {
                maxWidth = lineWidth;
            }
        }
        
        int yPos = startY;
        for (String line : instructions) {
            int lineWidth = g.getFontMetrics().stringWidth(line);
            int adjustedX = getWidth() / 2 - lineWidth / 2;
            g.drawString(line, adjustedX, yPos);
            yPos += 40;
        }
    }
    
    /**
     * Dibuja el pie de página de la pantalla de instrucciones.
     */
    private void drawFooter(Graphics g) {
        g.setFont(new Font("Arial", Font.ITALIC, 16));
        g.setColor(new Color(200, 200, 200, 220)); // Gris claro semitransparente
        String note = "¡Diviértete jugando!";
        int noteWidth = g.getFontMetrics().stringWidth(note);
        g.drawString(note, getWidth() / 2 - noteWidth / 2, getHeight() - 50);
    }
}