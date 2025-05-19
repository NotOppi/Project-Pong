package pong.game.view.interfaces;

import java.awt.Color;

/**
 * Interfaz para la pantalla de selección de dificultad
 */
public interface DifficultyScreenInterface extends ViewInterface {
    void setCurrentDifficulty(String difficultyName);
    void setMultiplayerMode(boolean isMultiplayer);
    void setBackgroundColor(Color color);
    void setTextColor(Color color);
    void setOverlayColor(Color color);
    void updateButtonThemes(Color buttonColor, Color buttonTextColor);
}