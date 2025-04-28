package pong.game.view;

import pong.game.model.GameModel;
import pong.game.controller.GameController;
import pong.game.controller.InputController;
import pong.game.view.screens.*;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.util.Observer;
import java.util.Observable;

/**
 * Panel principal que muestra el juego y delega a las pantallas específicas
 */
@SuppressWarnings("deprecation")
public class GamePanel extends JPanel implements Observer {
    
    private GameModel model;
    private GameController controller;
    private InputController inputController;
    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private InstructionsScreen instructionsScreen;
    private ThemeScreen themeScreen;
    private DifficultyScreen difficultyScreen;
    
    private enum ScreenState {MAIN_MENU, GAME, INSTRUCTIONS, DIFFICULTY, THEMES}
    
    public GamePanel() {
        // Configuración básica del panel
        setBackground(Color.BLACK);
        setFocusable(true);
        setLayout(null);
        
        // Inicializar modelo y controladores
        model = new GameModel();
        model.addObserver(this);
        
        controller = new GameController(model);
        inputController = new InputController(controller);
        addKeyListener(inputController);
        
        // Inicializar pantallas - con tamaños explícitos
        menuScreen = new MenuScreen(model, controller);
        gameScreen = new GameScreen(model);
        instructionsScreen = new InstructionsScreen(model);
        themeScreen = new ThemeScreen(model, controller);
        difficultyScreen = new DifficultyScreen(model, controller);
        
        // Establecer tamaños para todas las pantallas
        menuScreen.setBounds(0, 0, PongGame.WIDTH, PongGame.HEIGHT);
        gameScreen.setBounds(0, 0, PongGame.WIDTH, PongGame.HEIGHT);
        instructionsScreen.setBounds(0, 0, PongGame.WIDTH, PongGame.HEIGHT);
        themeScreen.setBounds(0, 0, PongGame.WIDTH, PongGame.HEIGHT);
        difficultyScreen.setBounds(0, 0, PongGame.WIDTH, PongGame.HEIGHT);
        
        // Agregar componentes de las pantallas
        add(menuScreen);
        add(gameScreen);
        add(instructionsScreen);
        add(themeScreen);
        add(difficultyScreen);

        controller.startGame();
        model.setGamePaused(false);
        
        // Mostrar la pantalla inicial
        showScreen(ScreenState.MAIN_MENU);
        
        // Iniciar el timer para actualizar el juego
        Timer gameTimer = new Timer(10, e -> {
            controller.update();
            repaint();
        });
        gameTimer.start();
    }
    
    private void showScreen(ScreenState screen) {
        
        // Siempre mantener gameScreen visible, pero en segundo plano
        gameScreen.setVisible(true);
        
        // Hacer que gameScreen esté al fondo y las otras pantallas en primer plano
        setComponentZOrder(gameScreen, getComponentCount() - 1);
        
        // Hacer visibles solo las pantallas necesarias
        menuScreen.setVisible(screen == ScreenState.MAIN_MENU);
        instructionsScreen.setVisible(screen == ScreenState.INSTRUCTIONS);
        themeScreen.setVisible(screen == ScreenState.THEMES);
        difficultyScreen.setVisible(screen == ScreenState.DIFFICULTY);
        
        // Si estamos en el menú, configurar modo demo
        if (screen == ScreenState.MAIN_MENU) {
            // Activar el modo demo
            model.setDemoMode(true);
            
            // Resetear puntuaciones para el modo demo
            model.setPlayerScore(0);
            model.setAiScore(0);
            
            // Si el juego no está corriendo, iniciarlo para la demo
            if (!model.isGameRunning()) {
                model.getBall().reset();
                model.getPlayerPaddle().reset();
                model.getAiPaddle().reset();
                model.setGameRunning(true);
                model.setGamePaused(false);
                model.setGameOver(false);
            }
        } else {
            // En otras pantallas, desactivar el modo demo
            model.setDemoMode(false);
        }
        
        // Si estamos en la pantalla de juego, asegurar que tiene el foco para recibir eventos
        if (screen == ScreenState.GAME) {
            setComponentZOrder(gameScreen, 0);
        }
        
        requestFocus(); // Asegurar que el panel sigue recibiendo eventos de teclado
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // El renderizado ahora es manejado por las pantallas específicas
        // Este método solo se encarga de pintar elementos comunes a todas las pantallas
        
        // Ejemplo: mostrar versión o copyright
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.setColor(Color.WHITE);
        g.drawString("Pong v1.0", 5, getHeight() - 5);
    }
    
    @Override
    public void update(Observable o, Object arg) {
        // Reaccionar a cambios en el modelo
        if (arg instanceof String) {
            String message = (String) arg;
            
            if (message.equals("SCREEN_CHANGE")) {
                String screenName = model.getCurrentScreen();
                switch (screenName) {
                    case "MAIN_MENU": showScreen(ScreenState.MAIN_MENU); break;
                    case "GAME": showScreen(ScreenState.GAME); break;
                    case "INSTRUCTIONS": showScreen(ScreenState.INSTRUCTIONS); break;
                    case "DIFFICULTY": showScreen(ScreenState.DIFFICULTY); break;
                    case "THEMES": showScreen(ScreenState.THEMES); break;
                    default: 
                        System.out.println("Pantalla no reconocida: " + screenName);
                        break;
                }
            }
        }
        
        repaint();
    }
}