package pong.game.model;

import java.awt.Color;

/**
 * Representa un tema de colores para el juego.
 */
public class Theme {
    private final String name;
    private final Color backgroundColor;
    private final Color paddleColor;
    private final Color ballColor;
    private final Color textColor;
    private final Color dividerColor;
    private final Color buttonColor;
    private final Color buttonTextColor;
    private final Color panelOverlayColor;
    
    /**
     * Crea un nuevo tema con los colores especificados
     * @param name nombre del tema
     * @param backgroundColor color de fondo
     * @param paddleColor color de las paletas
     * @param ballColor color de la pelota
     * @param textColor color del texto
     * @param dividerColor color del divisor
     * @param buttonColor color de los botones
     * @param buttonTextColor color del texto en los botones
     * @param panelOverlayColor color del panel superpuesto
     */
    public Theme(String name, Color backgroundColor, Color paddleColor, 
                Color ballColor, Color textColor, Color dividerColor,
                Color buttonColor, Color buttonTextColor, Color panelOverlayColor) {
        this.name = name;
        this.backgroundColor = backgroundColor;
        this.paddleColor = paddleColor;
        this.ballColor = ballColor;
        this.textColor = textColor;
        this.dividerColor = dividerColor;
        this.buttonColor = buttonColor;
        this.buttonTextColor = buttonTextColor;
        this.panelOverlayColor = panelOverlayColor;
    }
    
    // Métodos getter
    public String getName() { return name; }
    public Color getBackgroundColor() { return backgroundColor; }
    public Color getPaddleColor() { return paddleColor; }
    public Color getBallColor() { return ballColor; }
    public Color getTextColor() { return textColor; }
    public Color getDividerColor() { return dividerColor; }
    public Color getButtonColor() { return buttonColor; }
    public Color getButtonTextColor() { return buttonTextColor; }
    public Color getPanelOverlayColor() { return panelOverlayColor; }
    
    // Temas predefinidos
    public static final Theme CLASSIC = new Theme(
        "Classic",
        Color.BLACK,              // Fondo
        Color.WHITE,              // Paletas
        Color.WHITE,              // Pelota
        Color.WHITE,              // Texto
        Color.WHITE,              // Divisor
        new Color(70, 130, 180),  // Botón (Azul Acero)
        Color.WHITE,              // Texto de botón
        new Color(0, 0, 0, 220)   // Panel superpuesto
    );
    
    public static final Theme NEON = new Theme(
        "Neon",
        Color.BLACK,              // Fondo
        new Color(0, 255, 204),   // Paletas (Cian)
        new Color(255, 0, 128),   // Pelota (Rosa intenso)
        new Color(255, 255, 0),   // Texto (Amarillo)
        new Color(0, 255, 0),     // Divisor (Verde)
        new Color(128, 0, 255),   // Botón (Púrpura)
        Color.WHITE,              // Texto de botón
        new Color(0, 0, 40, 230)  // Panel superpuesto
    );
    
    public static final Theme RETRO = new Theme(
        "Retro",
        new Color(0, 51, 102),    // Fondo (Azul marino)
        new Color(255, 153, 51),  // Paletas (Naranja)
        new Color(255, 204, 0),   // Pelota (Amarillo)
        new Color(255, 255, 204), // Texto (Amarillo claro)
        new Color(204, 102, 0),   // Divisor (Marrón)
        new Color(153, 76, 0),    // Botón (Marrón oscuro)
        new Color(255, 255, 204), // Texto de botón
        new Color(0, 30, 60, 220) // Panel superpuesto
    );
    
    public static final Theme DARK = new Theme(
        "Dark",
        new Color(30, 30, 30),    // Fondo (Gris oscuro)
        new Color(100, 100, 100), // Paletas (Gris)
        new Color(200, 200, 200), // Pelota (Gris claro)
        new Color(200, 200, 200), // Texto (Gris claro)
        new Color(80, 80, 80),    // Divisor 
        new Color(60, 60, 60),    // Botón (Gris oscuro)
        new Color(200, 200, 200), // Texto de botón
        new Color(15, 15, 15, 230)// Panel superpuesto
    );
    
    // Array de todos los temas disponibles
    public static final Theme[] AVAILABLE_THEMES = {CLASSIC, NEON, RETRO, DARK};
}