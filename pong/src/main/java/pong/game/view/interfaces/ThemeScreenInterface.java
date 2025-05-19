package pong.game.view.interfaces;

import pong.game.controller.dto.ThemeDTO;
import java.awt.Color;
import java.util.List;

/**
 * Interfaz para la pantalla de selección de temas
 */
public interface ThemeScreenInterface extends ViewInterface {
    void setThemes(List<ThemeDTO> themes);
    void setCurrentTheme(String themeName);
    void setBackgroundColor(Color color);
    void setTextColor(Color color);
    void setOverlayColor(Color color);
    void updateButtonThemes(Color buttonColor, Color buttonTextColor);
    boolean needsThemeData();
    void setNeedsThemeData(boolean needs);
}