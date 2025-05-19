package pong.game.view.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import pong.game.controller.GameController;
import pong.game.view.ModernButton;
import pong.game.view.PongGame;
import pong.game.view.interfaces.MenuScreenInterface;

/**
 * Pantalla de menú principal del juego Pong.
 * Implementa el patrón MVC manteniendo una separación estricta entre
 * la visualización y la lógica de control del juego.
 */
public class MenuScreen extends JPanel implements MenuScreenInterface {
    // Constantes de dimensiones de botones
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 45;
    private static final int BUTTON_SPACING = 10;
    
    // Controlador del juego
    private GameController controller;
    
    // Componentes de UI
    private ModernButton startButton;
    private ModernButton howToPlayButton;
    private ModernButton difficultyButton;
    private ModernButton multiplayerButton;
    private ModernButton themeButton;
    
    // Almacena posiciones originales para restaurar después de GameOver
    private Map<ModernButton, Integer> originalButtonPositions;
    
    // Estado de la pantalla
    private boolean isGameOver = false;
    
    // Configuración de colores del tema actual
    private Color backgroundColor = Color.BLACK;
    private Color textColor = Color.WHITE;
    private Color buttonColor = new Color(70, 130, 180);
    private Color buttonTextColor = Color.WHITE;
    
    /**
     * Constructor de la pantalla de menú.
     * 
     * @param controller El controlador del juego que maneja la lógica
     */
    public MenuScreen(GameController controller) {
        this.controller = controller;
        setLayout(null);
        setOpaque(false);
        
        initializeButtons();
        controller.registerMenuScreen(this);
    }
    
    /**
     * Inicializa todos los botones del menú y calcula sus posiciones.
     */
    private void initializeButtons() {
        // Calcula el centrado vertical
        int totalButtonCount = 5; // Start, HowToPlay, Difficulty, Multiplayer, Theme
        int totalButtonsHeight = (BUTTON_HEIGHT * totalButtonCount) + (BUTTON_SPACING * (totalButtonCount - 1));
        int startY = (PongGame.HEIGHT - totalButtonsHeight) / 2;
        
        // Botón Iniciar Juego
        startButton = createButton("Iniciar Juego", startY, _ -> {
            controller.startGame();
            controller.navigateToGame();
        });
        
        // Botón Cómo Jugar
        howToPlayButton = createButton("¿Cómo Jugar?", startY + BUTTON_HEIGHT + BUTTON_SPACING, _ -> {
            controller.navigateToInstructions();
        });
        
        // Botón Dificultad
        difficultyButton = createButton("Nivel de Dificultad", startY + (BUTTON_HEIGHT + BUTTON_SPACING) * 2, _ -> {
            controller.navigateToDifficulty();
        });
        
        // Botón Multijugador
        multiplayerButton = createButton("Un Jugador", startY + (BUTTON_HEIGHT + BUTTON_SPACING) * 3, _ -> {
            controller.toggleMultiplayerMode();
        });
        
        // Botón Tema
        themeButton = createButton("Cambiar Tema", startY + (BUTTON_HEIGHT + BUTTON_SPACING) * 4, _ -> {
            controller.navigateToThemes();
        });
        
        // Almacena las posiciones originales de los botones
        originalButtonPositions = new HashMap<>();
        originalButtonPositions.put(startButton, startButton.getY());
        originalButtonPositions.put(howToPlayButton, howToPlayButton.getY());
        originalButtonPositions.put(difficultyButton, difficultyButton.getY());
        originalButtonPositions.put(multiplayerButton, multiplayerButton.getY());
        originalButtonPositions.put(themeButton, themeButton.getY());
    }
    
    /**
     * Crea un botón con configuraciones comunes y lo añade al panel.
     * 
     * @param text Texto a mostrar en el botón
     * @param y Posición vertical del botón
     * @param action Acción a ejecutar cuando se pulsa el botón
     * @return El botón creado
     */
    private ModernButton createButton(String text, int y, ActionListener action) {
        ModernButton button = new ModernButton(text);
        button.setBounds(PongGame.WIDTH / 2 - BUTTON_WIDTH / 2, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setFocusable(false);
        button.addActionListener(action);
        button.setButtonColor(buttonColor);
        button.setTextColor(buttonTextColor);
        
        add(button);
        return button;
    }
    
    /**
     * Reposiciona los botones para la pantalla de fin de juego.
     */
    private void repositionButtonsForGameOver() {
        int gameOverY = PongGame.HEIGHT / 2 + 30;
        int spacing = 55;
        
        startButton.setBounds(startButton.getX(), gameOverY, startButton.getWidth(), startButton.getHeight());
        howToPlayButton.setBounds(howToPlayButton.getX(), gameOverY + spacing, howToPlayButton.getWidth(), howToPlayButton.getHeight());
        difficultyButton.setBounds(difficultyButton.getX(), gameOverY + spacing * 2, difficultyButton.getWidth(), difficultyButton.getHeight());
        multiplayerButton.setBounds(multiplayerButton.getX(), gameOverY + spacing * 3, multiplayerButton.getWidth(), multiplayerButton.getHeight());
        themeButton.setBounds(themeButton.getX(), gameOverY + spacing * 4, themeButton.getWidth(), themeButton.getHeight());
    }
    
    /**
     * Restaura los botones a sus posiciones originales.
     */
    private void restoreButtonPositions() {
        startButton.setBounds(startButton.getX(), originalButtonPositions.get(startButton), startButton.getWidth(), startButton.getHeight());
        howToPlayButton.setBounds(howToPlayButton.getX(), originalButtonPositions.get(howToPlayButton), howToPlayButton.getWidth(), howToPlayButton.getHeight());
        difficultyButton.setBounds(difficultyButton.getX(), originalButtonPositions.get(difficultyButton), difficultyButton.getWidth(), difficultyButton.getHeight());
        multiplayerButton.setBounds(multiplayerButton.getX(), originalButtonPositions.get(multiplayerButton), multiplayerButton.getWidth(), multiplayerButton.getHeight());
        themeButton.setBounds(themeButton.getX(), originalButtonPositions.get(themeButton), themeButton.getWidth(), themeButton.getHeight());
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Dibujar un fondo semitransparente para mejorar la legibilidad
        g.setColor(new Color(0, 0, 0, 150)); // Negro semitransparente
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Dibujar título del juego
        g.setColor(textColor);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString("PONG", getWidth() / 2 - 70, 100);
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        
        if (visible) {
            // Si se muestra y el juego ha terminado, reposicionar botones para pantalla fin de juego
            if (isGameOver) {
                repositionButtonsForGameOver();
                startButton.setText("¿Jugar de nuevo?");
            } else {
                // De lo contrario, restaurar a posiciones originales
                restoreButtonPositions();
                startButton.setText("Iniciar Juego");
            }
        }
    }
    
    // Implementación de la interfaz MenuScreenInterface
    @Override
    public void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }
    
    @Override
    public void setMultiplayerButtonText(boolean isMultiplayer) {
        if (multiplayerButton != null) {
            multiplayerButton.setText(isMultiplayer ? "Modo Multijugador" : "Un Jugador");
        }
    }
    
    @Override
    public void setThemeColors(Color background, Color text, Color button, Color buttonText) {
        this.backgroundColor = background;
        this.textColor = text;
        this.buttonColor = button;
        this.buttonTextColor = buttonText;
        
        // Actualizar colores de los botones
        if (startButton != null) {
            startButton.setButtonColor(button);
            startButton.setTextColor(buttonText);
        }
        if (howToPlayButton != null) {
            howToPlayButton.setButtonColor(button);
            howToPlayButton.setTextColor(buttonText);
        }
        if (difficultyButton != null) {
            difficultyButton.setButtonColor(button);
            difficultyButton.setTextColor(buttonText);
        }
        if (multiplayerButton != null) {
            multiplayerButton.setButtonColor(button);
            multiplayerButton.setTextColor(buttonText);
        }
        if (themeButton != null) {
            themeButton.setButtonColor(button);
            themeButton.setTextColor(buttonText);
        }
    }
    
    @Override
    public void refresh() {
        repaint();
    }
}