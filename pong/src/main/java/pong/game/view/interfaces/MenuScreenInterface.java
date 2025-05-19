package pong.game.view.interfaces;

import java.awt.Color;

/**
 * Interfaz para la pantalla de menú
 */
public interface MenuScreenInterface extends ViewInterface {
    void setGameOver(boolean isGameOver);
    void setMultiplayerButtonText(boolean isMultiplayer);
    void setThemeColors(Color background, Color text, Color button, Color buttonText);
}