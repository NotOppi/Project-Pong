package pong.game.controller;

import pong.game.model.GameModel;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 * Maneja las entradas del teclado y las envía al controlador
 */
public class InputController implements KeyListener {
    private GameController controller;
    private static final int PADDLE_SPEED = 6;
    
    public InputController(GameController controller) {
        this.controller = controller;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        GameModel model = controller.getModel();
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_W && !model.isGamePaused() && !model.isGameOver()) {
            model.getPlayerPaddle().setYVelocity(-PADDLE_SPEED);
        }
        
        if (key == KeyEvent.VK_S && !model.isGamePaused() && !model.isGameOver()) {
            model.getPlayerPaddle().setYVelocity(PADDLE_SPEED);
        }
        
        // Controles del Jugador 2 en modo multijugador
        if (model.isMultiplayerMode() && !model.isGamePaused() && !model.isGameOver()) {
            if (key == KeyEvent.VK_UP) {
                model.getAiPaddle().setYVelocity(-PADDLE_SPEED);
            }
            if (key == KeyEvent.VK_DOWN) {
                model.getAiPaddle().setYVelocity(PADDLE_SPEED);
            }
        }
        
        // Tecla espacio - solo funciona si NO estamos en modo demo
        if (key == KeyEvent.VK_SPACE && !model.isDemoMode()) {
            if (model.isGameOver() || !model.isGameRunning()) {
                controller.startGame();
            } else {
                controller.pauseGame();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        GameModel model = controller.getModel();
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_W || key == KeyEvent.VK_S) {
            model.getPlayerPaddle().setYVelocity(0);
        }
        
        // Liberación de teclas del Jugador 2 en modo multijugador
        if (model.isMultiplayerMode()) {
            if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) {
                model.getAiPaddle().setYVelocity(0);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No utilizado
    }
}