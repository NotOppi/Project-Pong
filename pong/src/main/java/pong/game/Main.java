package pong.game;

import javax.swing.SwingUtilities;

/**
 * Clase principal que lanza el juego Pong.
 */
public class Main {
    /**
     * Punto de entrada para la aplicación
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PongGame());
    }
}