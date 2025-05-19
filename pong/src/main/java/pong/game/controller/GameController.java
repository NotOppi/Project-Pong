package pong.game.controller;

import pong.game.model.GameModel;
import pong.game.model.Ball;
import pong.game.model.Paddle;
import pong.game.model.Theme;
import pong.game.view.interfaces.*;
import pong.game.controller.GameController.NavigationListener;
import pong.game.controller.dto.ThemeDTO;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

/**
 * Controla la lógica del juego y actualiza el modelo
 * Sirve como intermediario entre el modelo y las vistas
 */
public class GameController {
    // Constantes del juego
    private static final int WINNING_SCORE = 10;
    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 600;
    private static final int SCORING_DELAY_MS = 1500;
    private static final int PADDLE_SPEED = 5;
    private static final int CENTER_POSITION_X = GAME_WIDTH / 2;
    private static final int CENTER_POSITION_Y = GAME_HEIGHT / 2;
    
    private GameModel model;
    
    // Referencias a las interfaces de vista
    private GameScreenInterface gameScreen;
    private MenuScreenInterface menuScreen;
    private InstructionsScreenInterface instructionsScreen;
    private ThemeScreenInterface themeScreen;
    private DifficultyScreenInterface difficultyScreen;
    
    // Lista de todas las vistas para actualizaciones generales
    private List<ViewInterface> views = new ArrayList<>();
    
    // Interfaz para manejar navegación entre pantallas
    private NavigationListener navigationListener;
    
    /**
     * Constructor que recibe el modelo
     */
    public GameController(GameModel model) {
        this.model = model;
        initializeModelDefaults();
    }
    
    private void initializeModelDefaults() {
        if (model.getCurrentTheme() == null) {
            model.setCurrentTheme(Theme.CLASSIC);
        }
        if (model.getCurrentDifficulty() == null) {
            model.setCurrentDifficulty(GameModel.Difficulty.MEDIUM);
        }
    }
    
    //region Registro de Vistas
    
    /**
     * Registra una vista para actualizaciones
     */
    public void registerView(ViewInterface view) {
        if (!views.contains(view)) {
            views.add(view);
        }
    }
    
    public void registerGameScreen(GameScreenInterface screen) {
        this.gameScreen = screen;
        registerView(screen);
    }
    
    public void registerMenuScreen(MenuScreenInterface screen) {
        this.menuScreen = screen;
        registerView(screen);
    }
    
    public void registerInstructionsScreen(InstructionsScreenInterface screen) {
        this.instructionsScreen = screen;
        registerView(screen);
    }
    
    public void registerThemeScreen(ThemeScreenInterface screen) {
        this.themeScreen = screen;
        registerView(screen);
    }
    
    public void registerDifficultyScreen(DifficultyScreenInterface screen) {
        this.difficultyScreen = screen;
        registerView(screen);
    }
    
    //endregion
    
    //region Actualización principal del juego
    
    /**
     * Actualiza el estado del juego y todas las vistas
     */
    public void update() {
        updateModelState();
        updateViews();
    }
    
    /**
     * Actualiza el estado del modelo
     */
    private void updateModelState() {
        if (model.isGamePaused()) {
            return;
        }
        
        if (model.isDemoMode()) {
            updateDemoMode();
            return;
        }
        
        model.getPlayerPaddle().update();
        
        if (!model.isGameRunning() || model.isGameOver()) {
            if (model.isGameOver()) {
                updateAIPaddleOnGameOver();
            }
            return;
        }
        
        updateOpponentPaddle();
        updateBallSpeed();
        model.getBall().update();
        checkCollision();
        checkScoring();
    }
    
    private void updateAIPaddleOnGameOver() {
        model.getAiPaddle().setYVelocity(0);
        model.getAiPaddle().update();
    }
    
    private void updateOpponentPaddle() {
        if (model.isMultiplayerMode()) {
            model.getAiPaddle().update();
        } else {
            updateAIPaddle();
        }
    }
    
    //endregion
    
    //region Lógica de puntuación
    
    /**
     * Verifica si algún jugador ha anotado
     */
    private void checkScoring() {
        if (shouldSkipScoringCheck()) {
            return;
        }
        
        Ball ball = model.getBall();
        
        // El jugador anota
        if (ball.getX() + ball.getWidth() >= GAME_WIDTH) {
            handleScore("player", model.isMultiplayerMode() ? "Jugador 1" : "Tú");
        }
        // La IA anota
        else if (ball.getX() <= 0) {
            handleScore("ai", model.isMultiplayerMode() ? "Jugador 2" : "IA");
        }
    }
    
    private boolean shouldSkipScoringCheck() {
        return model.isDemoMode() || model.isDelayAfterScore() || 
               !model.isGameRunning() || model.isGamePaused();
    }
    
    private void handleScore(String scorer, String winnerName) {
        if ("player".equals(scorer)) {
            model.incrementPlayerScore();
            if (model.getPlayerScore() >= WINNING_SCORE) {
                endGame(winnerName);
            }
        } else {
            model.incrementAiScore();
            if (model.getAiScore() >= WINNING_SCORE) {
                endGame(winnerName);
            }
        }
        
        model.setLastScorer(scorer);
        model.setDelayAfterScore(true);
        resetBallAfterScoring();
        startScoringDelay();
    }
    
    private void endGame(String winnerName) {
        model.setGameOver(true);
        model.setWinner(winnerName);
    }
    
    private void resetBallAfterScoring() {
        Ball ball = model.getBall();
        ball.setPosition((int)(CENTER_POSITION_X - ball.getWidth() / 2), 
                        (int)(CENTER_POSITION_Y - ball.getHeight() / 2));
        ball.setXVelocity(0);
        ball.setYVelocity(0);
    }
    
    /**
     * Inicia un retraso después de anotar un punto
     */
    private void startScoringDelay() {
        Timer delayTimer = new Timer(SCORING_DELAY_MS, e -> {
            model.setDelayAfterScore(false);
            
            if (!model.isGameOver()) {
                model.getBall().reset();
            }
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }
    
    //endregion
    
    //region Lógica de juego
    
    /**
     * Actualiza el modo demo del juego
     */
    private void updateDemoMode() {
        if (!model.isGameRunning()) {
            model.setGameRunning(true);
        }
        
        model.getBall().update();
        
        updateDemoPaddle(model.getPlayerPaddle(), true);
        updateDemoPaddle(model.getAiPaddle(), false);
        
        checkCollision();
        handleDemoBallReset();
    }
    
    private void handleDemoBallReset() {
        Ball ball = model.getBall();
        if (ball.getX() + ball.getWidth() >= GAME_WIDTH || ball.getX() <= 0) {
            ball.reset();
        }
    }
    
    /**
     * Actualiza una paleta en modo demo
     */
    private void updateDemoPaddle(Paddle paddle, boolean isLeftPaddle) {
        Ball ball = model.getBall();
        
        int paddleCenterY = paddle.y + paddle.height / 2;
        int ballCenterY = ball.y + ball.height / 2;
        
        boolean ballMovingTowardsPaddle = (isLeftPaddle && ball.getXVelocity() < 0) || 
                                         (!isLeftPaddle && ball.getXVelocity() > 0);
        
        float reactionSpeed = 0.7f;
        int targetY;
        
        if (ballMovingTowardsPaddle) {
            float timeToIntercept;
            
            if (isLeftPaddle) {
                timeToIntercept = Math.max(1, (paddle.x + paddle.width - ball.x) / 
                                Math.abs(ball.getXVelocity()));
            } else {
                timeToIntercept = Math.max(1, (ball.x - paddle.x) / 
                                Math.abs(ball.getXVelocity()));
            }
            
            float predictedY = ball.y + (ball.getYVelocity() * timeToIntercept);
            targetY = (int) predictedY + ball.height / 2;
        } else {
            targetY = GAME_HEIGHT / 2;
        }
        
        // Asegurar que la paleta permanezca dentro de los límites
        targetY = boundValue(targetY, paddle.height / 2, GAME_HEIGHT - paddle.height / 2);
        
        // Moverse hacia el objetivo
        updatePaddleMovement(paddle, paddleCenterY, targetY, reactionSpeed);
    }
    
    /**
     * Actualiza la velocidad de la paleta para moverse hacia un objetivo
     */
    private void updatePaddleMovement(Paddle paddle, int currentY, int targetY, float speedFactor) {
        if (currentY < targetY - 5) {
            paddle.setYVelocity((int)(PADDLE_SPEED * speedFactor));
        } else if (currentY > targetY + 5) {
            paddle.setYVelocity((int)(-PADDLE_SPEED * speedFactor));
        } else {
            paddle.setYVelocity(0);
        }
        
        paddle.update();
    }
    
    /**
     * Mantiene un valor dentro de los límites min y max
     */
    private int boundValue(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
    
    /**
     * Actualiza la velocidad de la pelota según la dificultad
     */
    private void updateBallSpeed() {
        Ball ball = model.getBall();
        
        if (model.getCurrentDifficulty() == GameModel.Difficulty.HARD && !model.isMultiplayerMode()) {
            ball.setSpeedMultiplier(1.2f);
        } else {
            ball.setSpeedMultiplier(1.0f);
        }
    }
    
    /**
     * Verifica colisiones entre la pelota y las paletas
     */
    private void checkCollision() {
        Ball ball = model.getBall();
        Paddle playerPaddle = model.getPlayerPaddle();
        Paddle aiPaddle = model.getAiPaddle();
        
        if (playerPaddle.intersects(ball)) {
            ball.deflectFromPaddle(playerPaddle);
        }
        
        if (aiPaddle.intersects(ball)) {
            ball.deflectFromPaddle(aiPaddle);
        }
    }
    
    /**
     * Actualiza la IA del oponente
     */
    private void updateAIPaddle() {
        Ball ball = model.getBall();
        Paddle aiPaddle = model.getAiPaddle();
        
        int aiPaddleCenterY = aiPaddle.y + aiPaddle.height / 2;
        int ballCenterY = ball.y + ball.height / 2;
        
        DifficultySettings settings = getDifficultySettings(model.getCurrentDifficulty());
        
        int targetY = calculateAITargetPosition(ball, aiPaddle, settings.predictFactor);
        targetY = boundValue(targetY, aiPaddle.height / 2, GAME_HEIGHT - aiPaddle.height / 2);
        
        int distanceToTarget = targetY - aiPaddleCenterY;
        
        if (Math.abs(distanceToTarget) > settings.deadZone) {
            aiPaddle.setYVelocity((int)(Math.signum(distanceToTarget) * PADDLE_SPEED * settings.reactionSpeed));
        } else {
            aiPaddle.setYVelocity(0);
        }
        
        aiPaddle.update();
    }
    
    private int calculateAITargetPosition(Ball ball, Paddle aiPaddle, float predictFactor) {
        int ballCenterY = ball.y + ball.height / 2;
        
        if (ball.getXVelocity() > 0) {
            float timeToIntercept = Math.max(1, (aiPaddle.x - ball.x - ball.width) / 
                                Math.abs(ball.getXVelocity()));
            float predictedY = ball.y + (ball.getYVelocity() * timeToIntercept);
            return (int)((predictedY + ball.height / 2) * predictFactor + ballCenterY * (1 - predictFactor));
        } else {
            return GAME_HEIGHT / 2;
        }
    }
    
    /**
     * Configuración para los diferentes niveles de dificultad
     */
    private static class DifficultySettings {
        float reactionSpeed;
        float predictFactor;
        int deadZone;
        
        DifficultySettings(float reactionSpeed, float predictFactor, int deadZone) {
            this.reactionSpeed = reactionSpeed;
            this.predictFactor = predictFactor;
            this.deadZone = deadZone;
        }
    }
    
    private DifficultySettings getDifficultySettings(GameModel.Difficulty difficulty) {
        switch (difficulty) {
            case EASY:
                return new DifficultySettings(0.5f, 0.5f, 10);
            case MEDIUM:
                return new DifficultySettings(0.7f, 0.7f, 7);
            case HARD:
                return new DifficultySettings(0.9f, 0.9f, 3);
            default:
                return new DifficultySettings(0.7f, 0.7f, 7);
        }
    }
    
    //endregion
    
    //region Actualización de vistas
    
    /**
     * Actualiza todas las vistas con datos del modelo
     */
    private void updateViews() {
        if (gameScreen != null) {
            updateGameScreen();
        }
        
        if (menuScreen != null) {
            updateMenuScreen();
        }
        
        if (instructionsScreen != null) {
            updateInstructionsScreen();
        }
        
        if (themeScreen != null) {
            updateThemeScreen();
        }
        
        if (difficultyScreen != null) {
            updateDifficultyScreen();
        }
    }
    
    /**
     * Actualiza la pantalla de juego con los datos actuales
     */
    private void updateGameScreen() {
        Theme theme = model.getCurrentTheme();
        
        // Configurar colores del tema
        gameScreen.setBackgroundColor(theme.getBackgroundColor());
        gameScreen.setTextColor(theme.getTextColor());
        gameScreen.setDividerColor(theme.getDividerColor());
        
        // Configurar datos de juego
        gameScreen.setScore(model.getPlayerScore(), model.getAiScore());
        gameScreen.setMultiplayerMode(model.isMultiplayerMode());
        gameScreen.setThemeName(theme.getName());
        
        // Datos de la pelota y paletas
        updateGameScreenObjects(theme);
        
        // Estados especiales
        updateGameScreenState();
        
        gameScreen.refresh();
    }
    
    private void updateGameScreenObjects(Theme theme) {
        Ball ball = model.getBall();
        gameScreen.setBallData(ball.x, ball.y, ball.width, ball.height, theme.getBallColor());
        
        Paddle playerPaddle = model.getPlayerPaddle();
        gameScreen.setPlayerPaddleData(
            playerPaddle.x, playerPaddle.y, playerPaddle.width, playerPaddle.height, 
            theme.getPaddleColor());
        
        Paddle aiPaddle = model.getAiPaddle();
        gameScreen.setAIPaddleData(
            aiPaddle.x, aiPaddle.y, aiPaddle.width, aiPaddle.height, 
            theme.getPaddleColor());
    }
    
    private void updateGameScreenState() {
        boolean isActiveGame = model.isGamePaused() && model.isGameRunning();
        gameScreen.showPauseScreen(isActiveGame);
        gameScreen.setExitButtonVisible(isActiveGame);
        
        // Manejo de mensajes de Game Over
        if (model.isGameOver()) {
            String winnerText = model.isMultiplayerMode() ?
                (model.getPlayerScore() > model.getAiScore() ? "Jugador 1" : "Jugador 2") :
                (model.getPlayerScore() > model.getAiScore() ? "¡Has ganado!" : "La IA");
            gameScreen.showGameOver(winnerText);
        } else {
            gameScreen.showGameOver(null);
        }
        
        // Mensaje de delay después de puntuar
        if (model.isDelayAfterScore() && model.isGameRunning() && !model.isGameOver()) {
            String message = getScoringMessage(model.getLastScorer());
            gameScreen.showDelayMessage(message);
        } else {
            gameScreen.showDelayMessage(null);
        }
    }
    
    private String getScoringMessage(String scorer) {
        if ("player".equals(scorer)) {
            return model.isMultiplayerMode() ? "¡Punto para Jugador 1!" : "¡Punto para ti!";
        } else {
            return model.isMultiplayerMode() ? "¡Punto para Jugador 2!" : "¡Punto para la IA!";
        }
    }
    
    /**
     * Actualiza la pantalla de menú con los datos actuales
     */
    private void updateMenuScreen() {
        Theme theme = model.getCurrentTheme();
        
        menuScreen.setGameOver(model.isGameOver());
        menuScreen.setMultiplayerButtonText(model.isMultiplayerMode());
        menuScreen.setThemeColors(
            theme.getBackgroundColor(),
            theme.getTextColor(),
            theme.getButtonColor(),
            theme.getButtonTextColor()
        );
        
        menuScreen.refresh();
    }
    
    /**
     * Actualiza la pantalla de instrucciones
     */
    private void updateInstructionsScreen() {
        Theme theme = model.getCurrentTheme();
        
        instructionsScreen.setMultiplayerMode(model.isMultiplayerMode());
        instructionsScreen.setBackgroundColor(theme.getBackgroundColor());
        instructionsScreen.setTextColor(theme.getTextColor());
        instructionsScreen.setOverlayColor(theme.getPanelOverlayColor());
        instructionsScreen.updateButtonThemes(theme.getButtonColor(), theme.getButtonTextColor());
        
        instructionsScreen.refresh();
    }
    
    /**
     * Actualiza la pantalla de temas
     */
    private void updateThemeScreen() {
        Theme currentTheme = model.getCurrentTheme();
        
        themeScreen.setBackgroundColor(currentTheme.getBackgroundColor());
        themeScreen.setTextColor(currentTheme.getTextColor());
        themeScreen.setOverlayColor(currentTheme.getPanelOverlayColor());
        themeScreen.setCurrentTheme(currentTheme.getName());
        themeScreen.updateButtonThemes(currentTheme.getButtonColor(), currentTheme.getButtonTextColor());
        
        if (themeScreen.needsThemeData()) {
            populateThemeData();
        }
        
        themeScreen.refresh();
    }
    
    private void populateThemeData() {
        List<ThemeDTO> themeDTOs = new ArrayList<>();
        for (Theme theme : Theme.AVAILABLE_THEMES) {
            themeDTOs.add(convertThemeToDTO(theme));
        }
        themeScreen.setThemes(themeDTOs);
        themeScreen.setNeedsThemeData(false);
    }
    
    /**
     * Actualiza la pantalla de dificultad
     */
    private void updateDifficultyScreen() {
        Theme theme = model.getCurrentTheme();
        
        difficultyScreen.setBackgroundColor(theme.getBackgroundColor());
        difficultyScreen.setTextColor(theme.getTextColor());
        difficultyScreen.setOverlayColor(theme.getPanelOverlayColor());
        difficultyScreen.updateButtonThemes(theme.getButtonColor(), theme.getButtonTextColor());
        
        difficultyScreen.setCurrentDifficulty(getDifficultyName(model.getCurrentDifficulty()));
        difficultyScreen.setMultiplayerMode(model.isMultiplayerMode());
        
        difficultyScreen.refresh();
    }
    
    private String getDifficultyName(GameModel.Difficulty difficulty) {
        switch (difficulty) {
            case EASY: return "Fácil";
            case MEDIUM: return "Medio";
            case HARD: return "Difícil";
            default: return "Medio";
        }
    }
    
    /**
     * Convierte un tema a DTO para pasar a la vista
     */
    private ThemeDTO convertThemeToDTO(Theme theme) {
        return new ThemeDTO(
            theme.getName(),
            theme.getBackgroundColor(),
            theme.getPaddleColor(),
            theme.getBallColor(),
            theme.getTextColor(),
            theme.getDividerColor(),
            theme.getButtonColor(),
            theme.getButtonTextColor(),
            theme.getPanelOverlayColor()
        );
    }
    
    //endregion
    
    //region Métodos de control para las vistas
    
    /**
     * Inicia un nuevo juego
     */
    public void startGame() {
        model.getBall().reset();
        model.getPlayerPaddle().reset();
        model.getAiPaddle().reset();
        
        model.setPlayerScore(0);
        model.setAiScore(0);
        model.setGameRunning(true);
        model.setGameOver(false);
        model.setWinner("");
        model.setGamePaused(false);
        model.setDemoMode(false);
    }
    
    /**
     * Pausa o reanuda el juego
     */
    public void pauseGame() {
        model.setGamePaused(!model.isGamePaused());
    }
    
    /**
     * Establece la dificultad del juego
     */
    public void setDifficulty(GameModel.Difficulty difficulty) {
        model.setCurrentDifficulty(difficulty);
    }
    
    /**
     * Establece el tema del juego
     */
    public void setTheme(Theme theme) {
        if (theme != null) {
            System.out.println("Cambiando tema a: " + theme.getName());
            model.setCurrentTheme(theme);
            updateViews();
        }
    }
    
    /**
     * Alterna entre modo un jugador y multijugador
     */
    public void toggleMultiplayerMode() {
        model.setMultiplayerMode(!model.isMultiplayerMode());
    }
    
    /**
     * Regresa al menú principal desde el juego
     */
    public void exitToMainMenu() {
        model.setGamePaused(false);
        navigateToMainMenu();
    }
    
    /**
     * Obtiene un tema según su nombre
     */
    public Theme getThemeByName(String name) {
        if (name != null) {
            for (Theme theme : Theme.AVAILABLE_THEMES) {
                if (theme.getName().equals(name)) {
                    return theme;
                }
            }
        }
        return Theme.CLASSIC;
    }
    
    /**
     * Devuelve el modelo para el InputController
     */
    public GameModel getModel() {
        return model;
    }
    
    //endregion
    
    //region Navegación entre pantallas
    
    /**
     * Interfaz para manejar navegación entre pantallas
     */
    public interface NavigationListener {
        void showScreen(String screenName);
    }
    
    /**
     * Establece el listener para navegación
     */
    public void setNavigationListener(NavigationListener listener) {
        this.navigationListener = listener;
    }
    
    /**
     * Notifica cambio de pantalla al listener
     */
    private void notifyScreenChange(String screenName) {
        if (navigationListener != null) {
            System.out.println("Navegando a: " + screenName);
            navigationListener.showScreen(screenName);
        } else {
            System.err.println("Error: NavigationListener no configurado");
        }
    }
    
    /**
     * Navega a la pantalla del menú principal
     */
    public void navigateToMainMenu() {
        model.setCurrentScreen("MAIN_MENU");
        notifyScreenChange("MAIN_MENU");
    }
    
    /**
     * Navega a la pantalla del juego
     */
    public void navigateToGame() {
        model.setCurrentScreen("GAME");
        notifyScreenChange("GAME");
    }
    
    /**
     * Navega a la pantalla de instrucciones
     */
    public void navigateToInstructions() {
        model.setCurrentScreen("INSTRUCTIONS");
        notifyScreenChange("INSTRUCTIONS");
    }
    
    /**
     * Navega a la pantalla de selección de dificultad
     */
    public void navigateToDifficulty() {
        model.setCurrentScreen("DIFFICULTY");
        notifyScreenChange("DIFFICULTY");
    }
    
    /**
     * Navega a la pantalla de selección de temas
     */
    public void navigateToThemes() {
        model.setCurrentScreen("THEMES");
        notifyScreenChange("THEMES");
    }
    
    //endregion
}