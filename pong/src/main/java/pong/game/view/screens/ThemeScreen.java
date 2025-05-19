package pong.game.view.screens;

import pong.game.controller.GameController;
import pong.game.controller.dto.ThemeDTO;
import pong.game.model.Theme;
import pong.game.view.ModernButton;
import pong.game.view.PongGame;
import pong.game.view.interfaces.ThemeScreenInterface;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla de selección de temas - MVC estricto
 */
public class ThemeScreen extends JPanel implements ThemeScreenInterface {
    // Controlador
    private GameController controller;
    
    // Componentes UI
    private ModernButton[] themeButtons;
    private ModernButton closeButton;
    
    // Estado de la pantalla
    private boolean needsThemeData = true;
    private String hoverDescription = "";
    
    // Datos de estado mantenidos localmente
    private Color backgroundColor = Color.BLACK;
    private Color textColor = Color.WHITE;
    private Color overlayColor = new Color(0, 0, 0, 180);
    private String currentThemeName = "Classic";
    private List<ThemeDTO> themes = new ArrayList<>();
    
    /**
     * Constructor de la pantalla de temas
     * @param controller Controlador del juego
     */
    public ThemeScreen(GameController controller) {
        this.controller = controller;
        controller.registerThemeScreen(this);
        
        setLayout(null);
        setOpaque(false);
        
        // Configurar botón de cerrar
        initializeCloseButton();
        
        // Los botones de tema se inicializarán cuando se establezca la lista de temas
    }
    
    /**
     * Inicializa el botón para cerrar la pantalla
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
     * Inicializa los botones de temas disponibles
     */
    private void initializeThemeButtons() {
        // Primero eliminar botones anteriores si existen
        if (themeButtons != null) {
            for (ModernButton button : themeButtons) {
                if (button != null) {
                    remove(button);
                }
            }
        }
        
        themeButtons = new ModernButton[themes.size()];
        
        for (int i = 0; i < themes.size(); i++) {
            ThemeDTO theme = themes.get(i);
            final String themeName = theme.getName();
            
            themeButtons[i] = new ModernButton(themeName);
            themeButtons[i].setBounds(PongGame.WIDTH / 2 - 100, 150 + (i * 60), 200, 45);
            themeButtons[i].setFocusable(false);
            
            // Color fijo para cada botón de tema
            themeButtons[i].setButtonColor(theme.getButtonColor());
            themeButtons[i].setTextColor(theme.getButtonTextColor());
            
            // Desactivar el repintado automático durante eventos del mouse
            themeButtons[i].addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    hoverDescription = "Tema: " + themeName + "\n" +
                                 "Cambia los colores de los elementos del juego.";
                    repaint();
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    hoverDescription = "";
                    repaint();
                }
            });
            
            // ActionListener directo que usa invokeLater para evitar problemas de concurrencia
            final int index = i;
            themeButtons[i].addActionListener(e -> {
                javax.swing.SwingUtilities.invokeLater(() -> {
                    System.out.println("Botón clickeado: " + themeName);
                    Theme selectedTheme = controller.getThemeByName(themeName);
                    if (selectedTheme != null) {
                        controller.setTheme(selectedTheme);
                        controller.navigateToMainMenu();
                    }
                });
            });
            
            add(themeButtons[i]);
        }
        
        // Asegurar que los componentes se reorganicen correctamente
        revalidate();
    }
    
    // Métodos de interfaz ThemeScreenInterface
    @Override
    public boolean needsThemeData() {
        return needsThemeData;
    }

    @Override
    public void setNeedsThemeData(boolean needs) {
        this.needsThemeData = needs;
    }
    
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
    public void setCurrentTheme(String themeName) {
        this.currentThemeName = themeName;
    }
    
    @Override
    public void setThemes(List<ThemeDTO> themes) {
        this.themes = themes;
        initializeThemeButtons();
    }
    
    @Override
    public void refresh() {
        repaint();
    }

    @Override
    public void updateButtonThemes(Color buttonColor, Color buttonTextColor) {
        // Solo actualizar el botón de cerrar con los colores del tema actual
        if (closeButton != null) {
            closeButton.setButtonColor(buttonColor);
            closeButton.setTextColor(buttonTextColor);
        }
    }
    
    // Métodos sobreescritos de JPanel
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            // Asegura que todos los componentes se actualicen cuando la pantalla se hace visible
            revalidate();
            repaint();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Dibuja overlay semitransparente
        g.setColor(overlayColor);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Dibuja título
        g.setColor(textColor);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Seleccionar Tema", getWidth() / 2 - 140, 80);
        
        // Dibuja tema actual
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Actual: " + currentThemeName, getWidth() / 2 - 80, 120);
        
        // Dibuja descripción al pasar el ratón
        if (!hoverDescription.isEmpty()) {
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            drawMultiLineText(g, hoverDescription, getWidth() / 2 - 150, 450);
        }
    }
    
    /**
     * Dibuja texto en múltiples líneas
     */
    private void drawMultiLineText(Graphics g, String text, int x, int y) {
        for (String line : text.split("\n")) {
            g.drawString(line, x, y);
            y += g.getFontMetrics().getHeight();
        }
    }
}