package pong.game.view.screens;

import pong.game.controller.GameController;
import pong.game.view.ModernButton;
import pong.game.view.PongGame;
import pong.game.view.interfaces.GameScreenInterface;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

/**
 * Pantalla del juego en sí. Maneja la representación visual del juego Pong,
 * incluyendo la pelota, las paletas, puntuaciones y mensajes de estado.
 */
public class GameScreen extends JPanel implements GameScreenInterface {
    
    private GameController controller;
    private ModernButton exitToMenuButton;
    
    // Propiedades de la pelota
    private int ballX, ballY, ballWidth, ballHeight;
    private Color ballColor = Color.WHITE;
    
    // Propiedades de la paleta del jugador
    private int playerPaddleX, playerPaddleY, playerPaddleWidth, playerPaddleHeight;
    private Color playerPaddleColor = Color.WHITE;
    
    // Propiedades de la paleta de la IA o segundo jugador
    private int aiPaddleX, aiPaddleY, aiPaddleWidth, aiPaddleHeight;
    private Color aiPaddleColor = Color.WHITE;
    
    // Propiedades de puntuación
    private int playerScore = 0;
    private int aiScore = 0;
    
    // Propiedades de configuración
    private boolean isMultiplayerMode = false;
    private String themeName = "Classic";
    private Color backgroundColor = Color.BLACK;
    private Color textColor = Color.WHITE;
    private Color dividerColor = Color.WHITE;
    
    // Estado del juego
    private boolean showPause = false;
    private boolean gameOver = false;
    private String winnerText = null;
    private String delayMessage = null;
    
    /**
     * Constructor de la pantalla de juego.
     *
     * @param controller Controlador que gestiona la lógica del juego
     */
    public GameScreen(GameController controller) {
        this.controller = controller;
        setLayout(null);
        setOpaque(false);
        
        initializeComponents();
        controller.registerGameScreen(this);
    }
    
    /**
     * Inicializa los componentes de la interfaz de usuario.
     */
    private void initializeComponents() {
        // Crear botón para salir al menú desde la pausa
        exitToMenuButton = new ModernButton("Volver al Menú");
        exitToMenuButton.setBounds(PongGame.WIDTH / 2 - 100, PongGame.HEIGHT / 2 + 70, 200, 45);
        exitToMenuButton.setFocusable(false);
        exitToMenuButton.addActionListener(_ -> {
            controller.exitToMainMenu();
        });
        exitToMenuButton.setVisible(false);
        add(exitToMenuButton);
    }
    
    //--------------------------------------
    // Métodos de actualización de estado
    //--------------------------------------
    
    @Override
    public void setBallData(int x, int y, int width, int height, Color color) {
        this.ballX = x;
        this.ballY = y;
        this.ballWidth = width;
        this.ballHeight = height;
        this.ballColor = color;
    }
    
    @Override
    public void setPlayerPaddleData(int x, int y, int width, int height, Color color) {
        this.playerPaddleX = x;
        this.playerPaddleY = y;
        this.playerPaddleWidth = width;
        this.playerPaddleHeight = height;
        this.playerPaddleColor = color;
    }
    
    @Override
    public void setAIPaddleData(int x, int y, int width, int height, Color color) {
        this.aiPaddleX = x;
        this.aiPaddleY = y;
        this.aiPaddleWidth = width;
        this.aiPaddleHeight = height;
        this.aiPaddleColor = color;
    }
    
    @Override
    public void setScore(int playerScore, int aiScore) {
        this.playerScore = playerScore;
        this.aiScore = aiScore;
    }
    
    @Override
    public void setMultiplayerMode(boolean isMultiplayer) {
        this.isMultiplayerMode = isMultiplayer;
    }
    
    @Override
    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }
    
    @Override
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }
    
    @Override
    public void setTextColor(Color color) {
        this.textColor = color;
    }
    
    @Override
    public void setDividerColor(Color color) {
        this.dividerColor = color;
    }
    
    //--------------------------------------
    // Métodos de control de UI
    //--------------------------------------
    
    @Override
    public void showPauseScreen(boolean show) {
        this.showPause = show;
    }
    
    @Override
    public void setExitButtonVisible(boolean visible) {
        exitToMenuButton.setVisible(visible);
    }
    
    @Override
    public void showGameOver(String winner) {
        this.winnerText = winner;
        this.gameOver = winner != null;
    }
    
    @Override
    public void showDelayMessage(String message) {
        this.delayMessage = message;
    }
    
    @Override
    public void refresh() {
        repaint();
    }
    
    //--------------------------------------
    // Métodos de renderizado
    //--------------------------------------
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Dibuja el fondo
        g.setColor(backgroundColor);
        g.fillRect(0, 0, PongGame.WIDTH, PongGame.HEIGHT);
        
        // Dibuja elementos del juego
        drawPaddles(g);
        drawBall(g);
        drawDivider(g);
        drawScores(g);
        
        // Dibuja mensajes de estado
        if (delayMessage != null) {
            drawDelayMessage(g);
        }
        
        if (gameOver && winnerText != null) {
            drawGameOverMessage(g);
        } else if (showPause) {
            drawPauseMessage(g);
        }
        
        // Dibuja información del modo de juego
        drawGameInfo(g);
    }
    
    /**
     * Dibuja las paletas de ambos jugadores.
     */
    private void drawPaddles(Graphics g) {
        g.setColor(playerPaddleColor);
        g.fillRect(playerPaddleX, playerPaddleY, playerPaddleWidth, playerPaddleHeight);
        
        g.setColor(aiPaddleColor);
        g.fillRect(aiPaddleX, aiPaddleY, aiPaddleWidth, aiPaddleHeight);
    }
    
    /**
     * Dibuja la pelota solo si el juego no ha terminado.
     */
    private void drawBall(Graphics g) {
        if (winnerText == null) {
            g.setColor(ballColor);
            g.fillOval(ballX, ballY, ballWidth, ballHeight);
        }
    }
    
    /**
     * Dibuja la línea central divisoria.
     */
    private void drawDivider(Graphics g) {
        g.setColor(dividerColor);
        for (int i = 0; i < PongGame.HEIGHT; i += 50) {
            g.fillRect(PongGame.WIDTH / 2 - 1, i, 2, 25);
        }
    }
    
    /**
     * Dibuja las puntuaciones de ambos jugadores.
     */
    private void drawScores(Graphics g) {
        g.setColor(textColor);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        
        if (isMultiplayerMode) {
            g.drawString("P1: " + playerScore, PongGame.WIDTH / 2 - 80, 50);
            g.drawString("P2: " + aiScore, PongGame.WIDTH / 2 + 20, 50);
        } else {
            g.drawString(String.valueOf(playerScore), PongGame.WIDTH / 2 - 50, 50);
            g.drawString(String.valueOf(aiScore), PongGame.WIDTH / 2 + 30, 50);
        }
    }
    
    /**
     * Dibuja el mensaje de retraso (countdown).
     */
    private void drawDelayMessage(Graphics g) {
        g.setColor(new Color(255, 215, 0, 220));
        g.setFont(new Font("Arial", Font.BOLD, 30));
        int messageWidth = g.getFontMetrics().stringWidth(delayMessage);
        g.drawString(delayMessage, PongGame.WIDTH / 2 - messageWidth / 2, PongGame.HEIGHT / 2);
    }
    
    /**
     * Dibuja el mensaje de fin de juego.
     */
    private void drawGameOverMessage(Graphics g) {
        g.setColor(new Color(255, 215, 0, 220)); // Oro semitransparente
        g.setFont(new Font("Arial", Font.BOLD, 50));
        
        String finDelJuego = "FIN DEL JUEGO";
        int finDelJuegoWidth = g.getFontMetrics().stringWidth(finDelJuego);
        g.drawString(finDelJuego, PongGame.WIDTH / 2 - finDelJuegoWidth / 2, PongGame.HEIGHT / 2 - 50);
        
        g.setFont(new Font("Arial", Font.BOLD, 30));
        String ganadorTexto = winnerText + " Gana!";
        int ganadorTextoWidth = g.getFontMetrics().stringWidth(ganadorTexto);
        g.drawString(ganadorTexto, PongGame.WIDTH / 2 - ganadorTextoWidth / 2, PongGame.HEIGHT / 2);
        
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String instrucciones = "Presiona ESPACIO para jugar de nuevo";
        int instruccionesWidth = g.getFontMetrics().stringWidth(instrucciones);
        g.drawString(instrucciones, PongGame.WIDTH / 2 - instruccionesWidth / 2, PongGame.HEIGHT / 2 + 40);
    }
    
    /**
     * Dibuja el mensaje de pausa.
     */
    private void drawPauseMessage(Graphics g) {
        g.setColor(new Color(255, 255, 255, 200)); // Blanco semitransparente
        g.setFont(new Font("Arial", Font.BOLD, 50));
        
        String pausaTexto = "PAUSA";
        int pausaWidth = g.getFontMetrics().stringWidth(pausaTexto);
        g.drawString(pausaTexto, PongGame.WIDTH / 2 - pausaWidth / 2, PongGame.HEIGHT / 2 - 50);
        
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String continuarTexto = "Presiona ESPACIO para continuar";
        int continuarWidth = g.getFontMetrics().stringWidth(continuarTexto);
        g.drawString(continuarTexto, PongGame.WIDTH / 2 - continuarWidth / 2, PongGame.HEIGHT / 2);
    }
    
    /**
     * Dibuja información del modo de juego y tema.
     */
    private void drawGameInfo(Graphics g) {
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.setColor(textColor);
        g.drawString(isMultiplayerMode ? "Modo Multijugador" : "Un Jugador", 10, PongGame.HEIGHT - 25);
        g.drawString("Tema: " + themeName, 10, PongGame.HEIGHT - 10);
    }
}