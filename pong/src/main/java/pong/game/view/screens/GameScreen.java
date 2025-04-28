package pong.game.view.screens;

import pong.game.model.GameModel;
import pong.game.view.ModernButton;
import pong.game.view.PongGame;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

/**
 * Pantalla del juego en sí
 */
public class GameScreen extends JPanel {
    private GameModel model;
    private ModernButton exitToMenuButton;
    
    public GameScreen(GameModel model) {
        this.model = model;
        setLayout(null);
        setOpaque(false);
        
        // Crear botón para salir al menú desde la pausa
        exitToMenuButton = new ModernButton("Volver al Menú");
        exitToMenuButton.setBounds(PongGame.WIDTH / 2 - 100, PongGame.HEIGHT / 2 + 70, 200, 45);
        exitToMenuButton.setFocusable(false);
        exitToMenuButton.addActionListener(_ -> {
            // Terminar la partida y volver al menú principal
            model.setGameRunning(false);
            model.setGamePaused(false);
            
            // Reiniciar posiciones
            model.getPlayerPaddle().reset();
            model.getAiPaddle().reset();
            model.getBall().reset();
            
            model.setCurrentScreen("MAIN_MENU");
        });
        exitToMenuButton.setVisible(false);
        add(exitToMenuButton);
        
        // Aplicar tema al botón
        if (model.getCurrentTheme() != null) {
            exitToMenuButton.applyTheme(model.getCurrentTheme());
        }
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        
        // Asegurarse de que el botón de salir solo aparece cuando se pausa
        if (visible) {
            exitToMenuButton.setVisible(model.isGamePaused() && model.isGameRunning());
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Actualiza la visibilidad del botón según el estado de pausa
        exitToMenuButton.setVisible(model.isGamePaused() && model.isGameRunning());
        
        // Dibuja el fondo (usando el tema actual)
        g.setColor(model.getCurrentTheme().getBackgroundColor());
        g.fillRect(0, 0, PongGame.WIDTH, PongGame.HEIGHT);
        
        // Dibuja las paletas
        model.getPlayerPaddle().draw(g);
        model.getAiPaddle().draw(g);
        
        // Dibuja la pelota (solo si el juego está en ejecución, no ha terminado y no hay delay después de punto)
        if (model.isGameRunning() && !model.isGameOver() && !model.isDelayAfterScore()) {
            model.getBall().draw(g);
        }
        
        // Si hay delay después de anotar un punto, mostrar mensaje
        if (model.isDelayAfterScore() && model.isGameRunning() && !model.isGameOver()) {
            String message;
            if (model.getLastScorer().equals("player")) {
                message = model.isMultiplayerMode() ? "¡Punto para Jugador 1!" : "¡Punto para ti!";
            } else {
                message = model.isMultiplayerMode() ? "¡Punto para Jugador 2!" : "¡Punto para la IA!";
            }
            
            // Dibujar un mensaje en el centro
            g.setColor(new Color(255, 215, 0, 220)); // Color amarillo dorado semitransparente
            g.setFont(new Font("Arial", Font.BOLD, 30));
            
            // Centrar el texto
            int messageWidth = g.getFontMetrics().stringWidth(message);
            g.drawString(message, PongGame.WIDTH / 2 - messageWidth / 2, PongGame.HEIGHT / 2);
        }
        
        // Dibuja la línea central divisoria
        g.setColor(model.getCurrentTheme().getDividerColor());
        for (int i = 0; i < PongGame.HEIGHT; i += 50) {
            g.fillRect(PongGame.WIDTH / 2 - 1, i, 2, 25);
        }
        
        // Dibuja las puntuaciones con etiquetas apropiadas
        g.setColor(model.getCurrentTheme().getTextColor());
        g.setFont(new Font("Arial", Font.BOLD, 30));
        
        if (model.isMultiplayerMode()) {
            // En modo multijugador, muestra etiquetas P1 y P2
            g.drawString("P1: " + model.getPlayerScore(), PongGame.WIDTH / 2 - 80, 50);
            g.drawString("P2: " + model.getAiScore(), PongGame.WIDTH / 2 + 20, 50);
        } else {
            // En modo un jugador, muestra solo las puntuaciones
            g.drawString(String.valueOf(model.getPlayerScore()), PongGame.WIDTH / 2 - 50, 50);
            g.drawString(String.valueOf(model.getAiScore()), PongGame.WIDTH / 2 + 30, 50);
        }
        
        // Dibuja mensaje de fin de juego
        if (model.isGameOver()) {
            g.setColor(new Color(255, 215, 0, 220)); // Oro semitransparente
            g.setFont(new Font("Arial", Font.BOLD, 50));
            
            // Centrar texto usando FontMetrics
            String finDelJuego = "FIN DEL JUEGO";
            int finDelJuegoWidth = g.getFontMetrics().stringWidth(finDelJuego);
            g.drawString(finDelJuego, PongGame.WIDTH / 2 - finDelJuegoWidth / 2, PongGame.HEIGHT / 2 - 50);
            
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String ganadorTexto = model.getWinner() + " Gana!";
            int ganadorTextoWidth = g.getFontMetrics().stringWidth(ganadorTexto);
            g.drawString(ganadorTexto, PongGame.WIDTH / 2 - ganadorTextoWidth / 2, PongGame.HEIGHT / 2);
            
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            String instrucciones = "Presiona ESPACIO para jugar de nuevo";
            int instruccionesWidth = g.getFontMetrics().stringWidth(instrucciones);
            g.drawString(instrucciones, PongGame.WIDTH / 2 - instruccionesWidth / 2, PongGame.HEIGHT / 2 + 40);
        }
        // Dibuja mensaje de pausa si el juego está en pausa y no ha terminado
        else if (model.isGamePaused() && model.isGameRunning()) {
            g.setColor(new Color(255, 255, 255, 200)); // Blanco semitransparente
            g.setFont(new Font("Arial", Font.BOLD, 50));
            
            // Centrar texto usando FontMetrics
            String pausaTexto = "PAUSA";
            int pausaWidth = g.getFontMetrics().stringWidth(pausaTexto);
            g.drawString(pausaTexto, PongGame.WIDTH / 2 - pausaWidth / 2, PongGame.HEIGHT / 2 - 50);
            
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            String continuarTexto = "Presiona ESPACIO para continuar";
            int continuarWidth = g.getFontMetrics().stringWidth(continuarTexto);
            g.drawString(continuarTexto, PongGame.WIDTH / 2 - continuarWidth / 2, PongGame.HEIGHT / 2);
        }
        
        // Muestra el modo de juego y tema en la esquina
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.setColor(model.getCurrentTheme().getTextColor());
        g.drawString(model.isMultiplayerMode() ? "Modo Multijugador" : "Un Jugador", 10, PongGame.HEIGHT - 25);
        g.drawString("Tema: " + model.getCurrentTheme().getName(), 10, PongGame.HEIGHT - 10);
    }
}