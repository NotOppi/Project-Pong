package pong.game.controller.dto;

import java.awt.Color;

/**
 * DTO para transferir datos de tema del controlador a las vistas
 */
public class ThemeDTO {
    private final String name;
    private final Color backgroundColor;
    private final Color paddleColor;
    private final Color ballColor;
    private final Color textColor;
    private final Color dividerColor;
    private final Color buttonColor;
    private final Color buttonTextColor;
    private final Color panelOverlayColor;
    
    public ThemeDTO(String name, Color backgroundColor, Color paddleColor, 
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
    
    public String getName() { return name; }
    public Color getBackgroundColor() { return backgroundColor; }
    public Color getPaddleColor() { return paddleColor; }
    public Color getBallColor() { return ballColor; }
    public Color getTextColor() { return textColor; }
    public Color getDividerColor() { return dividerColor; }
    public Color getButtonColor() { return buttonColor; }
    public Color getButtonTextColor() { return buttonTextColor; }
    public Color getPanelOverlayColor() { return panelOverlayColor; }
}