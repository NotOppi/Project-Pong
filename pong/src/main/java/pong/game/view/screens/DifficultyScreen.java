package pong.game.view.screens;

import pong.game.controller.GameController;
import pong.game.model.GameModel;
import pong.game.view.ModernButton;
import pong.game.view.PongGame;
import pong.game.view.interfaces.DifficultyScreenInterface;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Pantalla de selección de dificultad - MVC estricto
 * Permite al usuario seleccionar el nivel de dificultad para el juego.
 */
public class DifficultyScreen extends JPanel implements DifficultyScreenInterface {
    // Componentes UI
    private ModernButton easyButton;
    private ModernButton mediumButton;
    private ModernButton hardButton;
    private ModernButton closeButton;
    
    // Controlador
    private GameController controller;
    
    // Estado de la interfaz
    private String hoverDescription = "";
    private Color backgroundColor = Color.BLACK;
    private Color textColor = Color.WHITE;
    private Color overlayColor = new Color(0, 0, 0, 180);
    private String currentDifficulty = "Medio";
    private boolean isMultiplayerMode = false;
    
    /**
     * Constructor de la pantalla de dificultad.
     * @param controller El controlador del juego
     */
    public DifficultyScreen(GameController controller) {
        this.controller = controller;
        controller.registerDifficultyScreen(this);
        
        setLayout(null);
        setOpaque(false);
        
        initializeCloseButton();
        initializeDifficultyButtons();
    }
    
    /**
     * Inicializa el botón de cierre de la pantalla.
     */
    private void initializeCloseButton() {
        closeButton = new ModernButton("X", true);
        closeButton.setBounds(720, 20, 40, 40);
        closeButton.setFocusable(false);
        closeButton.addActionListener(_ -> {
            controller.navigateToMainMenu();
        });
        add(closeButton);
    }
    
    /**
     * Inicializa los botones de selección de dificultad.
     */
    private void initializeDifficultyButtons() {
        // Botón Fácil
        easyButton = createDescriptiveButton("Fácil", 150, 
            "La IA se mueve lentamente y tiene tiempos de reacción largos,\n" +
            "permitiéndote anticipar y devolver la pelota con facilidad.\n" +
            "La velocidad de la pelota es estándar.",
            _ -> {
                controller.setDifficulty(GameModel.Difficulty.EASY);
                controller.navigateToMainMenu();
            });
        
        // Botón Medio
        mediumButton = createDescriptiveButton("Medio", 210, 
            "La IA tiene mayor precisión y tiempos de reacción más cortos,\n" +
            "ofreciendo un desafío equilibrado sin aumentar la velocidad de la pelota.",
            _ -> {
                controller.setDifficulty(GameModel.Difficulty.MEDIUM);
                controller.navigateToMainMenu();
            });
        
        // Botón Difícil
        hardButton = createDescriptiveButton("Difícil", 270, 
            "La IA anticipa mejor tus tiros y se mueve con gran agilidad;\n" +
            "además, la pelota viaja un poco más rápido para aumentar la intensidad.",
            _ -> {
                controller.setDifficulty(GameModel.Difficulty.HARD);
                controller.navigateToMainMenu();
            });
    }
    
    /**
     * Crea un botón con configuraciones comunes.
     * @param text Texto del botón
     * @param y Posición vertical
     * @param action Acción al hacer clic
     * @return El botón creado
     */
    private ModernButton createButton(String text, int y, ActionListener action) {
        ModernButton button = new ModernButton(text);
        button.setBounds(PongGame.WIDTH / 2 - 100, y, 200, 45);
        button.setFocusable(false);
        button.addActionListener(action);
        add(button);
        return button;
    }
    
    /**
     * Crea un botón con descripción al pasar el ratón.
     * @param text Texto del botón
     * @param y Posición vertical
     * @param hoverDesc Descripción al pasar el ratón
     * @param action Acción al hacer clic
     * @return El botón creado con descripción
     */
    private ModernButton createDescriptiveButton(String text, int y, String hoverDesc, ActionListener action) {
        ModernButton button = createButton(text, y, action);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                hoverDescription = hoverDesc;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent evt) {
                hoverDescription = "";
                repaint();
            }
        });
        return button;
    }
    
    /**
     * Dibuja texto multilínea en la pantalla.
     * @param g Contexto gráfico
     * @param text Texto a dibujar
     * @param x Posición horizontal
     * @param y Posición vertical
     */
    private void drawMultiLineText(Graphics g, String text, int x, int y) {
        // Calcular ancho máximo para centrado
        int maxWidth = 0;
        String[] lines = text.split("\n");
        for (String line : lines) {
            int lineWidth = g.getFontMetrics().stringWidth(line);
            if (lineWidth > maxWidth) {
                maxWidth = lineWidth;
            }
        }
        
        // Dibujar cada línea centrada horizontalmente
        for (String line : lines) {
            int lineWidth = g.getFontMetrics().stringWidth(line);
            int adjustedX = x + (maxWidth - lineWidth) / 2;
            g.drawString(line, adjustedX, y);
            y += g.getFontMetrics().getHeight();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Dibuja overlay semitransparente
        g.setColor(overlayColor);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Dibuja título - Centrado mejorado
        g.setColor(textColor);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        String title = "Selecciona Dificultad";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, getWidth() / 2 - titleWidth / 2, 80);
        
        // Dibuja dificultad actual - Centrado mejorado
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String currentText = "Actual: " + currentDifficulty;
        int currentWidth = g.getFontMetrics().stringWidth(currentText);
        g.drawString(currentText, getWidth() / 2 - currentWidth / 2, 120);
        
        // Dibuja descripción al pasar el ratón, si hay alguna
        if (!hoverDescription.isEmpty()) {
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            drawMultiLineText(g, hoverDescription, PongGame.WIDTH / 2 - 220, 340);
        }
        
        // Muestra nota sobre modo multijugador si corresponde
        if (isMultiplayerMode) {
            g.setFont(new Font("Arial", Font.ITALIC, 16));
            g.setColor(new Color(255, 215, 0, 220)); 
            
            String note = "Nota: La configuración de dificultad solo aplica en modo un jugador";
            int noteWidth = g.getFontMetrics().stringWidth(note);
            g.drawString(note, getWidth() / 2 - noteWidth / 2, 450);
        }
    }
    
    // Implementaciones de DifficultyScreenInterface
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
    public void setCurrentDifficulty(String difficultyName) {
        this.currentDifficulty = difficultyName;
    }
    
    @Override
    public void setMultiplayerMode(boolean isMultiplayer) {
        this.isMultiplayerMode = isMultiplayer;
    }
    
    @Override
    public void updateButtonThemes(Color buttonColor, Color buttonTextColor) {
        if (easyButton != null) {
            easyButton.setButtonColor(buttonColor);
            easyButton.setTextColor(buttonTextColor);
        }
        if (mediumButton != null) {
            mediumButton.setButtonColor(buttonColor);
            mediumButton.setTextColor(buttonTextColor);
        }
        if (hardButton != null) {
            hardButton.setButtonColor(buttonColor);
            hardButton.setTextColor(buttonTextColor);
        }
        if (closeButton != null) {
            closeButton.setButtonColor(buttonColor);
            closeButton.setTextColor(buttonTextColor);
        }
    }
    
    @Override
    public void refresh() {
        repaint();
    }
}