package pong.game.model;

import java.util.Observable;

/**
 * Modelo central que mantiene el estado del juego
 */
@SuppressWarnings("deprecation")
public class GameModel extends Observable {
    // Estados del juego
    private int playerScore = 0;
    private int aiScore = 0;
    private boolean gameRunning = false;
    private boolean gamePaused = false;
    private boolean gameOver = false;
    private boolean isMultiplayerMode = false;
    private boolean isDemoMode = false;
    private boolean isDelayAfterScore = false; // Nuevo estado para el retraso
    private String winner = "";
    private String currentScreen = "MAIN_MENU";
    private String lastScorer = ""; // "player" o "ai" para saber quién anotó
    
    // Objetos del juego
    private Ball ball;
    private Paddle playerPaddle;
    private Paddle aiPaddle;
    
    // Configuración de juego
    private Theme currentTheme;
    private Difficulty currentDifficulty;
    
    public enum Difficulty {EASY, MEDIUM, HARD}
    
    public GameModel() {
        this.currentTheme = Theme.CLASSIC;
        this.currentDifficulty = Difficulty.MEDIUM;
        initializeGameObjects();
    }
    
    /**
     * Inicializa los objetos del juego
     */
    private void initializeGameObjects() {
        // Dimensiones generales
        final int WIDTH = 800;
        final int HEIGHT = 600;
        final int PADDLE_WIDTH = 15;
        final int PADDLE_HEIGHT = 80;
        final int PADDLE_OFFSET = 30;
        final int BALL_SIZE = 15;
        
        playerPaddle = new Paddle(
            PADDLE_OFFSET, 
            HEIGHT / 2 - PADDLE_HEIGHT / 2, 
            PADDLE_WIDTH, 
            PADDLE_HEIGHT, 
            currentTheme.getPaddleColor()
        );
        
        aiPaddle = new Paddle(
            WIDTH - PADDLE_OFFSET - PADDLE_WIDTH, 
            HEIGHT / 2 - PADDLE_HEIGHT / 2, 
            PADDLE_WIDTH, 
            PADDLE_HEIGHT, 
            currentTheme.getPaddleColor()
        );
        
        ball = new Ball(
            WIDTH / 2, 
            HEIGHT / 2, 
            BALL_SIZE
        );
        ball.setColor(currentTheme.getBallColor());
    }
    
    // Métodos para actualizar el estado y notificar a los observadores
    public void incrementPlayerScore() {
        playerScore++;
        setChanged();
        notifyObservers("SCORE_UPDATE");
    }
    
    public void incrementAiScore() {
        aiScore++;
        setChanged();
        notifyObservers("SCORE_UPDATE");
    }

    // Implementar getters y setters para todos los atributos
    public int getPlayerScore() {
        return playerScore;
    }
    
    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
        setChanged();
        notifyObservers("SCORE_UPDATE");
    }
    
    public int getAiScore() {
        return aiScore;
    }
    
    public void setAiScore(int aiScore) {
        this.aiScore = aiScore;
        setChanged();
        notifyObservers("SCORE_UPDATE");
    }
    
    public boolean isGameRunning() {
        return gameRunning;
    }
    
    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
        setChanged();
        notifyObservers("GAME_STATE_CHANGE");
    }
    
    public boolean isGamePaused() {
        return gamePaused;
    }
    
    public void setGamePaused(boolean gamePaused) {
        this.gamePaused = gamePaused;
        setChanged();
        notifyObservers("GAME_STATE_CHANGE");
    }
    
    public boolean isGameOver() {
        return gameOver;
    }
    
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
        setChanged();
        notifyObservers("GAME_STATE_CHANGE");
    }
    
    public boolean isMultiplayerMode() {
        return isMultiplayerMode;
    }

    public boolean isDemoMode() {
        return isDemoMode;
    }
    
    public void setDemoMode(boolean isDemoMode) {
        this.isDemoMode = isDemoMode;
        setChanged();
        notifyObservers("GAME_MODE_CHANGE");
    }
    
    public void setMultiplayerMode(boolean isMultiplayerMode) {
        this.isMultiplayerMode = isMultiplayerMode;
        setChanged();
        notifyObservers("GAME_MODE_CHANGE");
    }
    
    public String getWinner() {
        return winner;
    }
    
    public void setWinner(String winner) {
        this.winner = winner;
        setChanged();
        notifyObservers("GAME_STATE_CHANGE");
    }
    
    public Ball getBall() {
        return ball;
    }
    
    public Paddle getPlayerPaddle() {
        return playerPaddle;
    }
    
    public Paddle getAiPaddle() {
        return aiPaddle;
    }
    
    public Theme getCurrentTheme() {
        return currentTheme;
    }
    
    public void setCurrentTheme(Theme currentTheme) {
        this.currentTheme = currentTheme;
        
        // Actualizar colores de los elementos del juego
        if (playerPaddle != null) {
            playerPaddle.setColor(currentTheme.getPaddleColor());
        }
        if (aiPaddle != null) {
            aiPaddle.setColor(currentTheme.getPaddleColor());
        }
        if (ball != null) {
            ball.setColor(currentTheme.getBallColor());
        }
        
        setChanged();
        notifyObservers("THEME_CHANGE");
    }
    
    public Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }
    
    public void setCurrentDifficulty(Difficulty currentDifficulty) {
        this.currentDifficulty = currentDifficulty;
        setChanged();
        notifyObservers("DIFFICULTY_CHANGE");
    }
    
    public String getCurrentScreen() {
        return currentScreen;
    }
    
    public void setCurrentScreen(String currentScreen) {
        this.currentScreen = currentScreen;
        setChanged();
        notifyObservers("SCREEN_CHANGE");
    }

    public boolean isDelayAfterScore() {
        return isDelayAfterScore;
    }
    
    public void setDelayAfterScore(boolean isDelayAfterScore) {
        this.isDelayAfterScore = isDelayAfterScore;
        setChanged();
        notifyObservers("GAME_STATE_CHANGE");
    }
    
    public String getLastScorer() {
        return lastScorer;
    }
    
    public void setLastScorer(String scorer) {
        this.lastScorer = scorer;
        setChanged();
        notifyObservers("SCORE_UPDATE");
    }

}