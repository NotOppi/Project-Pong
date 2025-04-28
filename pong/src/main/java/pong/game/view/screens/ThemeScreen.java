package pong.game.view.screens;

import pong.game.model.GameModel;
import pong.game.controller.GameController;
import pong.game.model.Theme;
import pong.game.view.ModernButton;
import pong.game.view.PongGame;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Font;

/**
 * Pantalla de selección de temas
 */
public class ThemeScreen extends JPanel {
    private GameModel model;
    private GameController controller;
    private ModernButton[] themeButtons;
    private ModernButton closeButton;
    private String hoverDescription = "";
    
    public ThemeScreen(GameModel model, GameController controller) {
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
        
        // Inicializar botones de tema
        initializeThemeButtons();
        
        // Aplicar tema
        if (model.getCurrentTheme() != null) {
            closeButton.applyTheme(model.getCurrentTheme());
        }
    }
    
    private void initializeThemeButtons() {
        themeButtons = new ModernButton[Theme.AVAILABLE_THEMES.length];
    
        for (int i = 0; i < Theme.AVAILABLE_THEMES.length; i++) {
            Theme theme = Theme.AVAILABLE_THEMES[i];
            final int themeIndex = i;
            
            themeButtons[i] = new ModernButton(theme.getName());
            themeButtons[i].setBounds(PongGame.WIDTH / 2 - 100, 150 + (i * 60), 200, 45);
            themeButtons[i].setFocusable(false);
            
            // Descripción al pasar el ratón
            final String desc = "Tema: " + theme.getName() + "\n" +
                               "Cambia los colores de los elementos del juego.";
            
            themeButtons[i].addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    hoverDescription = desc;
                    repaint();
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    hoverDescription = "";
                    repaint();
                }
            });
            
            themeButtons[i].addActionListener(_ -> {
                controller.setTheme(Theme.AVAILABLE_THEMES[themeIndex]);
                model.setCurrentScreen("MAIN_MENU");
            });
            
            add(themeButtons[i]);
            
            // Aplicar tema actual
            if (model.getCurrentTheme() != null) {
                themeButtons[i].applyTheme(model.getCurrentTheme());
            }
        }
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
        g.drawString("Seleccionar Tema", getWidth() / 2 - 140, 80);
        
        // Dibuja tema actual
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Actual: " + model.getCurrentTheme().getName(), getWidth() / 2 - 80, 120);
        
        // Dibuja descripción al pasar el ratón, si hay alguna
        if (!hoverDescription.isEmpty()) {
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            drawMultiLineText(g, hoverDescription, getWidth() / 2 - 150, 380);
        }
    }
    
    private void drawMultiLineText(Graphics g, String text, int x, int y) {
        for (String line : text.split("\n")) {
            g.drawString(line, x, y);
            y += g.getFontMetrics().getHeight();
        }
    }
}