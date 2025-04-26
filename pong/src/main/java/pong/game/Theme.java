package pong.game;

import java.awt.Color;

/**
 * Represents a color theme for the game.
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
     * Creates a new theme with the specified colors
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
    
    // Getters
    public String getName() { return name; }
    public Color getBackgroundColor() { return backgroundColor; }
    public Color getPaddleColor() { return paddleColor; }
    public Color getBallColor() { return ballColor; }
    public Color getTextColor() { return textColor; }
    public Color getDividerColor() { return dividerColor; }
    public Color getButtonColor() { return buttonColor; }
    public Color getButtonTextColor() { return buttonTextColor; }
    public Color getPanelOverlayColor() { return panelOverlayColor; }
    
    // Predefined themes
    public static final Theme CLASSIC = new Theme(
        "Classic",
        Color.BLACK,              // Background
        Color.WHITE,              // Paddles
        Color.WHITE,              // Ball
        Color.WHITE,              // Text
        Color.WHITE,              // Divider
        new Color(70, 130, 180),  // Button (Steel Blue)
        Color.WHITE,              // Button Text
        new Color(0, 0, 0, 220)   // Panel Overlay
    );
    
    public static final Theme NEON = new Theme(
        "Neon",
        Color.BLACK,              // Background
        new Color(0, 255, 204),   // Paddles (Cyan)
        new Color(255, 0, 128),   // Ball (Hot Pink)
        new Color(255, 255, 0),   // Text (Yellow)
        new Color(0, 255, 0),     // Divider (Green)
        new Color(128, 0, 255),   // Button (Purple)
        Color.WHITE,              // Button Text
        new Color(0, 0, 40, 230)  // Panel Overlay
    );
    
    public static final Theme RETRO = new Theme(
        "Retro",
        new Color(0, 51, 102),    // Background (Navy Blue)
        new Color(255, 153, 51),  // Paddles (Orange)
        new Color(255, 204, 0),   // Ball (Yellow)
        new Color(255, 255, 204), // Text (Light Yellow)
        new Color(204, 102, 0),   // Divider (Brown)
        new Color(153, 76, 0),    // Button (Dark Brown)
        new Color(255, 255, 204), // Button Text
        new Color(0, 30, 60, 220) // Panel Overlay
    );
    
    public static final Theme DARK = new Theme(
        "Dark",
        new Color(30, 30, 30),    // Background (Dark Gray)
        new Color(100, 100, 100), // Paddles (Gray)
        new Color(200, 200, 200), // Ball (Light Gray)
        new Color(200, 200, 200), // Text (Light Gray)
        new Color(80, 80, 80),    // Divider 
        new Color(60, 60, 60),    // Button (Dark Gray)
        new Color(200, 200, 200), // Button Text
        new Color(15, 15, 15, 230)// Panel Overlay
    );
    
    // Array of all available themes
    public static final Theme[] AVAILABLE_THEMES = {CLASSIC, NEON, RETRO, DARK};
}