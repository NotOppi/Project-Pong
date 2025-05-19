package pong.game.view.interfaces;

import java.awt.Color;

/**
 * Interfaz para la pantalla de instrucciones
 */
public interface InstructionsScreenInterface extends ViewInterface {
    void setMultiplayerMode(boolean isMultiplayer);
    void setBackgroundColor(Color color);
    void setTextColor(Color color);
    void setOverlayColor(Color color);
    void updateButtonThemes(Color buttonColor, Color buttonTextColor);
}