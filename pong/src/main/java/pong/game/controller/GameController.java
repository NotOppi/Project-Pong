package pong.game.controller;

import pong.game.model.GameModel;
import pong.game.model.Ball;
import pong.game.model.Paddle;
import pong.game.model.Theme;

/**
 * Controla la lógica del juego y actualiza el modelo
 */
public class GameController {
    private GameModel model;
    private static final int WINNING_SCORE = 10;
    
    public GameController(GameModel model) {
        this.model = model;
    }
    
    /**
     * Actualiza el estado del juego
     */
    public void update() {
        // Si el juego está pausado, no hacer nada
        if (model.isGamePaused()) {
            return;
        }
        
        // En modo demo, manejar ambas paletas con IA
        if (model.isDemoMode()) {
            updateDemoMode();
            return;
        }
        
        // Código existente para juego normal
        // Actualizar posición de la paleta del jugador siempre
        model.getPlayerPaddle().update();
        
        // Si el juego no está en curso o ha terminado, no actualizar más elementos
        if (!model.isGameRunning() || model.isGameOver()) {
            if (model.isGameOver()) {
                model.getAiPaddle().setYVelocity(0);
                model.getAiPaddle().update();
            }
            return;
        }
        
        // Actualizar la paleta del oponente
        if (model.isMultiplayerMode()) {
            model.getAiPaddle().update();
        } else {
            updateAIPaddle();
        }
        
        // Ajustar velocidad de la pelota según dificultad
        updateBallSpeed();
        
        // Actualizar elementos del juego
        model.getBall().update();
        checkCollision();
        checkScoring();
    }
    
    /**
     * Actualiza el juego en modo demo (ambas paletas controladas por IA)
     */
    private void updateDemoMode() {
        // Si el juego no está en curso, no hacer nada
        if (!model.isGameRunning()) {
            return;
        }
        
        // Ajustar velocidad de la pelota para demo (más suave)
        model.getBall().setSpeedMultiplier(1.0f);
        
        // Actualizar la pelota
        model.getBall().update();
        
        // Controlar la paleta del jugador con IA
        updateDemoPaddle(model.getPlayerPaddle(), true);
        
        // Controlar la paleta de la IA con IA
        updateDemoPaddle(model.getAiPaddle(), false);
        
        // Comprobar colisiones de la pelota con las paletas
        checkCollision();
        
        // Comprobar si la pelota sale de los límites
        // Nota: No usamos checkScoring() para evitar contar puntos
        checkScoring();  // Ya hemos modificado este método para manejar modo demo
    }
    
    /**
     * Actualiza una paleta en modo demo
     * @param paddle la paleta a actualizar
     * @param isLeftPaddle true si es la paleta izquierda, false si es la derecha
     */
    private void updateDemoPaddle(Paddle paddle, boolean isLeftPaddle) {
        Ball ball = model.getBall();
        
        // Centro de la paleta y de la pelota
        int paddleCenterY = paddle.y + paddle.height / 2;
        int ballCenterY = ball.y + ball.height / 2;
        
        // Determinar si la pelota va hacia esta paleta
        boolean ballMovingTowardsPaddle = (isLeftPaddle && ball.getXVelocity() < 0) || 
                                        (!isLeftPaddle && ball.getXVelocity() > 0);
        
        // Variables para el control de IA
        float reactionSpeed = 0.7f; // Velocidad media para demo
        float predictFactor = 0.8f; // Alta precisión para evitar fallas
        int deadZone = 5;           // Zona muerta pequeña para movimiento suave
        
        // Calcular el objetivo
        int targetY;
        
        if (ballMovingTowardsPaddle) {
            // Si la pelota va hacia la paleta, predecir trayectoria
            float timeToIntercept;
            
            if (isLeftPaddle) {
                timeToIntercept = Math.max(1, (paddle.x + paddle.width - ball.x) / 
                                Math.abs(ball.getXVelocity()));
            } else {
                timeToIntercept = Math.max(1, (ball.x - paddle.x) / 
                                Math.abs(ball.getXVelocity()));
            }
            
            // Predicción con pequeño error para comportamiento natural
            float predictedY = ball.y + (ball.getYVelocity() * timeToIntercept);
            
            // Aplicar factor de predicción
            targetY = (int)((predictedY + ball.height / 2) * predictFactor + ballCenterY * (1 - predictFactor));
        } else {
            // Si la pelota se aleja, volver gradualmente al centro
            targetY = 600 / 2;
        }
        
        // Limitar dentro de los bordes
        targetY = Math.max(targetY, paddle.height / 2);
        targetY = Math.min(targetY, 600 - paddle.height / 2);
        
        // Calcular la distancia al objetivo
        int distanceToTarget = targetY - paddleCenterY;
        
        // Movimiento suave con zona muerta
        if (Math.abs(distanceToTarget) > deadZone) {
            // La velocidad es proporcional a la distancia, limitada por la velocidad máxima
            float speed = Math.min(Math.abs(distanceToTarget) / 10.0f, 6.0f) * reactionSpeed;
            paddle.setYVelocity((int)(Math.signum(distanceToTarget) * speed));
        } else {
            // Detener cuando esté cerca del objetivo
            paddle.setYVelocity(0);
        }
        
        // Actualizar posición
        paddle.update();
    }
    
    /**
     * Actualiza la velocidad de la pelota según la dificultad actual
     */
    private void updateBallSpeed() {
        Ball ball = model.getBall();
        
        if (model.getCurrentDifficulty() == GameModel.Difficulty.HARD && !model.isMultiplayerMode()) {
            ball.setSpeedMultiplier(1.5f);
        } else if (model.getCurrentDifficulty() == GameModel.Difficulty.MEDIUM && !model.isMultiplayerMode()) {
            ball.setSpeedMultiplier(1.2f);
        } else {
            ball.setSpeedMultiplier(1.1f);
        }
    }
    
    public GameModel getModel() {
        return model;
    }

    /**
     * Actualiza el movimiento de la IA
     */
    private void updateAIPaddle() {
        Ball ball = model.getBall();
        Paddle aiPaddle = model.getAiPaddle();
        
        // Convertir a int para manejar valores double devueltos
        int aiPaddleCenterY = aiPaddle.y + aiPaddle.height / 2;
        int ballCenterY = ball.y + ball.height / 2;
        
        // Ajustar variables según la dificultad
        float reactionSpeed;
        float predictFactor;
        int deadZone;
        
        switch (model.getCurrentDifficulty()) {
            case EASY:
                reactionSpeed = 0.5f;
                predictFactor = 0.3f;
                deadZone = 15;
                break;
            case MEDIUM:
                reactionSpeed = 0.7f;
                predictFactor = 0.7f;
                deadZone = 10;
                break;
            case HARD:
                reactionSpeed = 0.9f;
                predictFactor = 0.95f;
                deadZone = 5;
                break;
            default:
                reactionSpeed = 0.7f;
                predictFactor = 0.7f;
                deadZone = 10;
        }
        
        // Para dificultades más altas, la IA intenta predecir la trayectoria
        int targetY = ballCenterY;
        
        // Si la pelota se mueve hacia la IA, intenta predecir su trayectoria
        if (ball.getXVelocity() > 0) {
            // Cálculo básico de predicción
            float timeToIntercept = (aiPaddle.x - ball.x) / (ball.getXVelocity() * model.getBall().getSpeedMultiplier());
            float predictedY = ball.y + (ball.getYVelocity() * model.getBall().getSpeedMultiplier() * timeToIntercept);
            
            // Aplicar factor de predicción según dificultad
            targetY = (int)((predictedY + ball.height / 2) * predictFactor + ballCenterY * (1 - predictFactor));
        } else {
            // Si la pelota se aleja, volver al centro gradualmente
            targetY = (int)(300 * 0.3f + ballCenterY * 0.7f);
        }
        
        // Limitar el objetivo dentro de los límites de la pantalla
        targetY = Math.max(targetY, aiPaddle.height / 2);
        targetY = Math.min(targetY, 600 - aiPaddle.height / 2);
        
        // Calcular la distancia al objetivo
        int distanceToTarget = targetY - aiPaddleCenterY;
        
        // Movimiento suave con zona muerta para evitar oscilaciones
        if (Math.abs(distanceToTarget) > deadZone) {
            // La velocidad es proporcional a la distancia, limitada por la velocidad máxima
            float speed = Math.min(Math.abs(distanceToTarget) / 10.0f, 6.0f) * reactionSpeed;
            aiPaddle.setYVelocity((int)(Math.signum(distanceToTarget) * speed));
        } else {
            // Detener el movimiento cuando esté cerca del objetivo
            aiPaddle.setYVelocity(0);
        }
        
        // LÍNEA CRUCIAL QUE FALTA: Actualizar la posición de la paleta de la IA
        aiPaddle.update();
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
     * Verifica si algún jugador ha anotado
     */
    private void checkScoring() {
        // Si estamos en modo demo, no contabilizar puntos
        if (model.isDemoMode()) {
            // Solo reiniciar la pelota cuando sale por los lados
            Ball ball = model.getBall();
            if (ball.getX() + ball.getWidth() >= 800 || ball.getX() <= 0) {
                ball.reset();
            }
            return;
        }
        
        // Si estamos en periodo de delay después de puntuar, no hacer nada
        if (model.isDelayAfterScore()) {
            return;
        }
        
        // Código normal de puntuación para el juego real
        Ball ball = model.getBall();
        
        // El jugador anota
        if (ball.getX() + ball.getWidth() >= 800) { // Usar constante WIDTH
            model.incrementPlayerScore();
            model.setLastScorer("player");
            
            // Iniciar delay después de puntuar
            startScoringDelay();
            
            // Comprueba condición de victoria
            if (model.getPlayerScore() >= WINNING_SCORE) {
                model.setGameOver(true);
                model.setWinner(model.isMultiplayerMode() ? "Jugador 1" : "Jugador");
            }
        }
        
        // La IA anota
        if (ball.getX() <= 0) {
            model.incrementAiScore();
            model.setLastScorer("ai");
            
            // Iniciar delay después de puntuar
            startScoringDelay();
            
            // Comprueba condición de victoria
            if (model.getAiScore() >= WINNING_SCORE) {
                model.setGameOver(true);
                model.setWinner(model.isMultiplayerMode() ? "Jugador 2" : "IA");
            }
        }
    }
    
    /**
     * Inicia un retraso después de anotar un punto
     */
    private void startScoringDelay() {
        // Activa el estado de retraso
        model.setDelayAfterScore(true);
        
        // Usa un temporizador para esperar antes de reiniciar la pelota
        javax.swing.Timer delayTimer = new javax.swing.Timer(1500, _ -> {
            // Después del retraso, reiniciar la pelota y desactivar el estado
            model.getBall().reset();
            model.setDelayAfterScore(false);
        });
        delayTimer.setRepeats(false); // Solo ejecuta una vez
        delayTimer.start();
    }
    
    // Métodos para manejar acciones de juego
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
    
    public void pauseGame() {
        model.setGamePaused(!model.isGamePaused());
    }
    
    public void setDifficulty(GameModel.Difficulty difficulty) {
        model.setCurrentDifficulty(difficulty);
    }
    
    public void setTheme(Theme theme) {
        model.setCurrentTheme(theme);
    }
    
    public void toggleMultiplayerMode() {
        model.setMultiplayerMode(!model.isMultiplayerMode());
    }
}