package pong.game.model;

/**
 * Main model that maintains the game state
 */
public class GameModel {
    // Constants
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 15;
    private static final int PADDLE_HEIGHT = 80;
    private static final int PADDLE_OFFSET = 30;
    private static final int BALL_SIZE = 15;
    
    // Game state
    private int playerScore = 0;
    private int aiScore = 0;
    private String winner = "";
    private String lastScorer = "";
    
    // Game flags
    private boolean gameRunning = false;
    private boolean gamePaused = false;
    private boolean gameOver = false;
    private boolean isMultiplayerMode = false;
    private boolean isDemoMode = false;
    private boolean isDelayAfterScore = false;
    
    // Navigation state
    private String currentScreen = "MAIN_MENU";
    
    // Game objects
    private Ball ball;
    private Paddle playerPaddle;
    private Paddle aiPaddle;
    
    // Game configuration
    private Theme currentTheme;
    private Difficulty currentDifficulty;
    
    public enum Difficulty {EASY, MEDIUM, HARD}
    
    public GameModel() {
        this.currentTheme = Theme.CLASSIC;
        this.currentDifficulty = Difficulty.MEDIUM;
        initializeGameObjects();
    }
    
    /**
     * Initializes game objects
     */
    private void initializeGameObjects() {
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
    
    // Score methods
    public void incrementPlayerScore() {
        playerScore++;
    }
    
    public void incrementAiScore() {
        aiScore++;
    }

    // Getters and setters grouped by category
    // Score getters/setters
    public int getPlayerScore() { return playerScore; }
    public void setPlayerScore(int playerScore) { this.playerScore = playerScore; }
    public int getAiScore() { return aiScore; }
    public void setAiScore(int aiScore) { this.aiScore = aiScore; }
    public String getLastScorer() { return lastScorer; }
    public void setLastScorer(String scorer) { this.lastScorer = scorer; }
    
    // Game state getters/setters
    public boolean isGameRunning() { return gameRunning; }
    public void setGameRunning(boolean gameRunning) { this.gameRunning = gameRunning; }
    public boolean isGamePaused() { return gamePaused; }
    public void setGamePaused(boolean gamePaused) { this.gamePaused = gamePaused; }
    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
    public String getWinner() { return winner; }
    public void setWinner(String winner) { this.winner = winner; }
    public boolean isDelayAfterScore() { return isDelayAfterScore; }
    public void setDelayAfterScore(boolean isDelayAfterScore) { this.isDelayAfterScore = isDelayAfterScore; }
    
    // Game mode getters/setters
    public boolean isMultiplayerMode() { return isMultiplayerMode; }
    public void setMultiplayerMode(boolean isMultiplayerMode) { this.isMultiplayerMode = isMultiplayerMode; }
    public boolean isDemoMode() { return isDemoMode; }
    public void setDemoMode(boolean isDemoMode) { this.isDemoMode = isDemoMode; }
    
    // Navigation getters/setters
    public String getCurrentScreen() { return currentScreen; }
    public void setCurrentScreen(String currentScreen) { this.currentScreen = currentScreen; }
    
    // Game objects getters
    public Ball getBall() { return ball; }
    public Paddle getPlayerPaddle() { return playerPaddle; }
    public Paddle getAiPaddle() { return aiPaddle; }
    
    // Configuration getters/setters
    public Theme getCurrentTheme() { return currentTheme; }
    public void setCurrentTheme(Theme currentTheme) {
        this.currentTheme = currentTheme;
        updateGameObjectColors();
    }
    
    private void updateGameObjectColors() {
        if (playerPaddle != null) {
            playerPaddle.setColor(currentTheme.getPaddleColor());
        }
        if (aiPaddle != null) {
            aiPaddle.setColor(currentTheme.getPaddleColor());
        }
        if (ball != null) {
            ball.setColor(currentTheme.getBallColor());
        }
    }
    
    public Difficulty getCurrentDifficulty() { return currentDifficulty; }
    public void setCurrentDifficulty(Difficulty currentDifficulty) { this.currentDifficulty = currentDifficulty; }
}
