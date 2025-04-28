package pong.game.view.screens;

import pong.game.model.GameModel;
import pong.game.controller.GameController;
import pong.game.view.ModernButton;
import pong.game.view.PongGame;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;

/**
 * Pantalla de selección de dificultad
 */
public class DifficultyScreen extends JPanel {
    private GameModel model;
    private GameController controller;
    private ModernButton easyButton;
    private ModernButton mediumButton;
    private ModernButton hardButton;
    private ModernButton backButton;
    private ModernButton closeButton;
    private String hoverDescription = "";
    
    public DifficultyScreen(GameModel model, GameController controller) {
        this.model = model;
        this.controller = controller;
        setLayout(null);
        setOpaque(false);
        
        // Configurar botón de cerrar
        closeButton = new ModernButton("X", true);
        closeButton.setBounds(720, 20, 40, 40);
        closeButton.setFocusable(false);
        closeButton.addActionListener(_ -> {
            model.setCurrentScreen("MAIN_MENU");
        });
        add(closeButton);
        
        // Inicializar botones de dificultad
        initializeDifficultyButtons();
        
        // Aplicar tema
        if (model.getCurrentTheme() != null) {
            applyThemeToButtons();
        }
    }
    
    private void initializeDifficultyButtons() {
        // Botón Fácil
        easyButton = createDescriptiveButton("Fácil", 150, 
            "La IA se mueve lentamente y tiene tiempos de reacción largos,\n" +
            "permitiéndote anticipar y devolver la pelota con facilidad.\n" +
            "La velocidad de la pelota es estándar.",
            _ -> {
                controller.setDifficulty(GameModel.Difficulty.EASY);
                model.setCurrentScreen("MAIN_MENU");
            });
        
        // Botón Medio
        mediumButton = createDescriptiveButton("Medio", 210, 
            "La IA tiene mayor precisión y tiempos de reacción más cortos,\n" +
            "ofreciendo un desafío equilibrado sin aumentar la velocidad de la pelota.",
            _ -> {
                controller.setDifficulty(GameModel.Difficulty.MEDIUM);
                model.setCurrentScreen("MAIN_MENU");
            });
        
        // Botón Difícil
        hardButton = createDescriptiveButton("Difícil", 270, 
            "La IA anticipa mejor tus tiros y se mueve con gran agilidad;\n" +
            "además, la pelota viaja un poco más rápido para aumentar la intensidad.",
            _ -> {
                controller.setDifficulty(GameModel.Difficulty.HARD);
                model.setCurrentScreen("MAIN_MENU");
            });
        
        // Botón Volver
        backButton = createButton("Volver", 380, _ -> {
            model.setCurrentScreen("MAIN_MENU");
        });
    }
    
    /**
     * Crea un botón con configuraciones comunes
     */
    private ModernButton createButton(String text, int y, java.awt.event.ActionListener action) {
        ModernButton button = new ModernButton(text);
        button.setBounds(PongGame.WIDTH / 2 - 100, y, 200, 45);
        button.setFocusable(false);
        button.addActionListener(action);
        
        // Aplica el tema actual
        if (model.getCurrentTheme() != null) {
            button.applyTheme(model.getCurrentTheme());
        }
        
        add(button);
        return button;
    }
    
    /**
     * Crea un botón con descripción al pasar el ratón
     */
    private ModernButton createDescriptiveButton(String text, int y, String hoverDesc, java.awt.event.ActionListener action) {
        ModernButton button = createButton(text, y, action);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                hoverDescription = hoverDesc;
                repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                hoverDescription = "";
                repaint();
            }
        });
        return button;
    }
    
    private void applyThemeToButtons() {
        if (easyButton != null) easyButton.applyTheme(model.getCurrentTheme());
        if (mediumButton != null) mediumButton.applyTheme(model.getCurrentTheme());
        if (hardButton != null) hardButton.applyTheme(model.getCurrentTheme());
        if (backButton != null) backButton.applyTheme(model.getCurrentTheme());
        if (closeButton != null) closeButton.applyTheme(model.getCurrentTheme());
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Solo dibuja si hay un tema configurado
        if (model.getCurrentTheme() == null) return;
        
        // Dibuja fondo semitransparente
        g.setColor(model.getCurrentTheme().getPanelOverlayColor());
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Dibuja título
        g.setColor(model.getCurrentTheme().getTextColor());
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Selecciona Dificultad", PongGame.WIDTH / 2 - 180, 80);
        
        // Dibuja dificultad actual
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String difficultyName;
        switch (model.getCurrentDifficulty()) {
            case EASY: difficultyName = "Fácil"; break;
            case MEDIUM: difficultyName = "Medio"; break;
            case HARD: difficultyName = "Difícil"; break;
            default: difficultyName = model.getCurrentDifficulty().toString(); break;
        }
        g.drawString("Actual: " + difficultyName, PongGame.WIDTH / 2 - 80, 120);
        
        // Dibuja descripción al pasar el ratón, si hay alguna
        if (!hoverDescription.isEmpty()) {
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            drawMultiLineText(g, hoverDescription, PongGame.WIDTH / 2 - 220, 330);
        }
        
        // Muestra nota de que la dificultad solo se aplica a un jugador
        if (model.isMultiplayerMode()) {
            g.setFont(new Font("Arial", Font.ITALIC, 16));
            g.setColor(Color.YELLOW);
            g.drawString("Nota: La configuración de dificultad solo aplica en modo un jugador", 
                         PongGame.WIDTH / 2 - 250, 430);
        }
    }
    
    private void drawMultiLineText(Graphics g, String text, int x, int y) {
        for (String line : text.split("\n")) {
            g.drawString(line, x, y);
            y += g.getFontMetrics().getHeight();
        }
    }
}