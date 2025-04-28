package pong.game.view.screens;

import pong.game.model.GameModel;
import pong.game.controller.GameController;
import pong.game.view.ModernButton;
import pong.game.view.PongGame;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

/**
 * Pantalla de menú principal
 */
public class MenuScreen extends JPanel {
    private GameModel model;
    private GameController controller;
    
    private ModernButton startButton;
    private ModernButton howToPlayButton;
    private ModernButton difficultyButton;
    private ModernButton multiplayerButton;
    private ModernButton themeButton;
    
    private Map<ModernButton, Integer> originalButtonPositions;
    
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 45;
    private static final int BUTTON_SPACING = 10;
    
    public MenuScreen(GameModel model, GameController controller) {
        this.model = model;
        this.controller = controller;
        setLayout(null);
        setOpaque(false);  // Asegurarse de que es transparente
        
        // Inicializar botones
        initializeButtons();
    }
    
    private void initializeButtons() {
        // Calcula el centrado vertical
        int totalButtonCount = 5; // Start, HowToPlay, Difficulty, Multiplayer, Theme
        int totalButtonsHeight = (BUTTON_HEIGHT * totalButtonCount) + (BUTTON_SPACING * (totalButtonCount - 1));
        int startY = (PongGame.HEIGHT - totalButtonsHeight) / 2;
        
        // Botón Iniciar Juego
        startButton = createButton("Iniciar Juego", startY, e -> {
            controller.startGame();
            model.setCurrentScreen("GAME");
        });
        
        // Botón Cómo Jugar
        howToPlayButton = createButton("Cómo Jugar", startY + BUTTON_HEIGHT + BUTTON_SPACING, e -> {
            model.setCurrentScreen("INSTRUCTIONS");
        });
        
        // Botón Dificultad
        difficultyButton = createButton("Nivel de Dificultad", startY + (BUTTON_HEIGHT + BUTTON_SPACING) * 2, e -> {
            model.setCurrentScreen("DIFFICULTY");
        });
        
        // Botón Multijugador
        multiplayerButton = createButton("Un Jugador", startY + (BUTTON_HEIGHT + BUTTON_SPACING) * 3, e -> {
            controller.toggleMultiplayerMode();
            multiplayerButton.setText(model.isMultiplayerMode() ? "Modo Multijugador" : "Un Jugador");
        });
        
        // Botón Tema
        themeButton = createButton("Cambiar Tema", startY + (BUTTON_HEIGHT + BUTTON_SPACING) * 4, e -> {
            model.setCurrentScreen("THEMES");
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
     * Crea un botón con configuraciones comunes
     */
    private ModernButton createButton(String text, int y, java.awt.event.ActionListener action) {
        ModernButton button = new ModernButton(text);
        button.setBounds(PongGame.WIDTH / 2 - BUTTON_WIDTH / 2, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setFocusable(false);
        button.addActionListener(action);
        
        // Aplica el tema actual
        if (model.getCurrentTheme() != null) {
            button.applyTheme(model.getCurrentTheme());
        }
        
        add(button);
        return button;
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        
        // Si se muestra y el juego ha terminado, reposicionar botones para pantalla fin de juego
        if (visible && model.isGameOver()) {
            repositionButtonsForGameOver();
        } else if (visible) {
            // De lo contrario, restaurar a posiciones originales
            restoreButtonPositions();
        }
        
        // Actualizar texto del botón Start según estado del juego
        if (visible) {
            if (model.isGameOver()) {
                startButton.setText("¿Jugar de nuevo?");
            } else {
                startButton.setText("Iniciar Juego");
            }
        }
    }
    
    private void repositionButtonsForGameOver() {
        int gameOverY = PongGame.HEIGHT / 2 + 30;
        int spacing = 55;
        
        startButton.setBounds(startButton.getX(), gameOverY, startButton.getWidth(), startButton.getHeight());
        howToPlayButton.setBounds(howToPlayButton.getX(), gameOverY + spacing, howToPlayButton.getWidth(), howToPlayButton.getHeight());
        difficultyButton.setBounds(difficultyButton.getX(), gameOverY + spacing * 2, difficultyButton.getWidth(), difficultyButton.getHeight());
        multiplayerButton.setBounds(multiplayerButton.getX(), gameOverY + spacing * 3, multiplayerButton.getWidth(), multiplayerButton.getHeight());
        themeButton.setBounds(themeButton.getX(), gameOverY + spacing * 4, themeButton.getWidth(), themeButton.getHeight());
    }
    
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
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString("PONG", getWidth() / 2 - 70, 100);
    }
}