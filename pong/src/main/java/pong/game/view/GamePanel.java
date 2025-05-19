package pong.game.view;

import pong.game.controller.GameController;
import pong.game.controller.InputController;
import pong.game.view.screens.*;
import pong.game.model.GameModel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

/**
 * Panel principal que muestra el juego y delega a las pantallas específicas
 * Implementación MVC estricta
 */
public class GamePanel extends JPanel {

    private GameController controller;
    private InputController inputController;
    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private InstructionsScreen instructionsScreen;
    private ThemeScreen themeScreen;
    private DifficultyScreen difficultyScreen;
    
    private enum ScreenState {MAIN_MENU, GAME, INSTRUCTIONS, DIFFICULTY, THEMES}
    private ScreenState currentScreen = ScreenState.MAIN_MENU;
    
    public GamePanel() {
        // Configuración básica del panel
        setBackground(Color.BLACK);
        setFocusable(true);
        setLayout(null);
        
        // Inicializar controlador con un nuevo modelo
        controller = new GameController(new pong.game.model.GameModel());
        
        // Configurar input controller
        inputController = new InputController(controller);
        addKeyListener(inputController);
        
        // Inicializar pantallas - todas usando el controlador
        menuScreen = new MenuScreen(controller);
        gameScreen = new GameScreen(controller);
        instructionsScreen = new InstructionsScreen(controller);
        themeScreen = new ThemeScreen(controller);
        difficultyScreen = new DifficultyScreen(controller);
        
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
        
        // Configurar navegación
        controller.setNavigationListener(this::showScreen);
        
        // Mostrar la pantalla inicial
        controller.navigateToMainMenu();
        
        // Iniciar el timer para actualizar el juego
        Timer gameTimer = new Timer(16, _ -> {
            controller.update();
        });
        gameTimer.start();
    }
    
    /**
     * Método para mostrar una pantalla específica
     */
    public void showScreen(String screenName) {
        ScreenState screen;
        switch (screenName) {
            case "MAIN_MENU": screen = ScreenState.MAIN_MENU; break;
            case "GAME": screen = ScreenState.GAME; break;
            case "INSTRUCTIONS": screen = ScreenState.INSTRUCTIONS; break;
            case "DIFFICULTY": screen = ScreenState.DIFFICULTY; break;
            case "THEMES": screen = ScreenState.THEMES; break;
            default: 
                System.out.println("Pantalla no reconocida: " + screenName);
                return;
        }
        showScreen(screen);
    }
    
    private void showScreen(ScreenState screen) {
        this.currentScreen = screen;
        
        // Siempre mantener gameScreen visible, pero en segundo plano
        gameScreen.setVisible(true);
        
        // Hacer que gameScreen esté al fondo y las otras pantallas en primer plano
        setComponentZOrder(gameScreen, getComponentCount() - 1);
        
        // Hacer visibles solo las pantallas necesarias
        menuScreen.setVisible(screen == ScreenState.MAIN_MENU);
        instructionsScreen.setVisible(screen == ScreenState.INSTRUCTIONS);
        themeScreen.setVisible(screen == ScreenState.THEMES);
        difficultyScreen.setVisible(screen == ScreenState.DIFFICULTY);
        
        // Si estamos en el menú, instrucciones, dificultad o temas, configurar modo demo
        // Solo desactivar la demo cuando estamos jugando realmente
        if (screen == ScreenState.GAME) {
            controller.getModel().setDemoMode(false);
        } else {
            controller.getModel().setDemoMode(true);
        }
        
        // Si estamos en la pantalla de juego, asegurar que tiene el foco
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
        
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.setColor(Color.WHITE);
        g.drawString("Pong v1.0", 5, getHeight() - 5);
    }
}