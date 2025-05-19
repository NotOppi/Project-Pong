package pong.game.view.interfaces;

import java.awt.Color;

/**
 * Interfaz para la pantalla de juego
 */
public interface GameScreenInterface extends ViewInterface {
    void setBallData(int x, int y, int width, int height, Color color);
    void setPlayerPaddleData(int x, int y, int width, int height, Color color);
    void setAIPaddleData(int x, int y, int width, int height, Color color);
    void setScore(int playerScore, int aiScore);
    void setMultiplayerMode(boolean isMultiplayer);
    void setThemeName(String themeName);
    void setBackgroundColor(Color color);
    void setTextColor(Color color);
    void setDividerColor(Color color);
    void showPauseScreen(boolean show);
    void showGameOver(String winner);
    void showDelayMessage(String message);
    void setExitButtonVisible(boolean visible);
}