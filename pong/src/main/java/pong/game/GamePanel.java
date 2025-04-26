package pong.game;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Panel que contiene los elementos del juego y su lógica.
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener {
    // Constantes del juego
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 45;
    private static final int BUTTON_SPACING = 10;
    private static final int WINNING_SCORE = 10;
    private static final int DELAY = 10;
    private static final int PADDLE_SPEED = 6;
    private static final int PADDLE_WIDTH = 15;
    private static final int PADDLE_HEIGHT = 80;
    private static final int PADDLE_OFFSET = 30;
    private static final int BALL_SIZE = 15;
    
    // Objetos del juego
    private Paddle playerPaddle;
    private Paddle aiPaddle;
    private Ball ball;
    
    // Componentes de interfaz
    private JButton startButton;
    private JButton howToPlayButton;
    private JButton difficultyButton;
    private JButton multiplayerButton;
    private JButton themeButton;
    private JButton closeInstructionsButton;
    private JButton easyButton;
    private JButton mediumButton;
    private JButton hardButton;
    private JButton backButton;
    private String hoverDescription = "";
    private Map<JButton, Integer> originalButtonPositions;
    private JButton[] themeButtons;
    
    // Estados del juego
    private enum ScreenState {MAIN_MENU, GAME, INSTRUCTIONS, DIFFICULTY, THEMES}
    private ScreenState currentScreen = ScreenState.MAIN_MENU;
    private int playerScore = 0;
    private int aiScore = 0;
    private boolean gameRunning = false;
    private boolean gamePaused = false;
    private boolean gameOver = false;
    private boolean isMultiplayerMode = false;
    private String winner = "";
    
    // Configuración de dificultad
    public enum Difficulty {EASY, MEDIUM, HARD}
    private Difficulty currentDifficulty = Difficulty.MEDIUM;
    private int aiPaddleSpeed = 4; // Predeterminado para MEDIUM
    private float aiReactionTime = 0.5f; // Predeterminado para MEDIUM
    
    // Configuración de tema
    private Theme currentTheme = Theme.CLASSIC;
    
    // Temporizador para el bucle de juego
    private Timer gameTimer;
    
    /**
     * Crea e inicializa el panel del juego
     */
    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        setLayout(null); // Usa posicionamiento absoluto
        
        // Inicializa componentes de interfaz
        initializeUI();
        
        // Inicializa objetos del juego
        initializeGameObjects();
        
        // Configura el temporizador del juego
        gameTimer = new Timer(DELAY, this);
        gameTimer.start(); // Inicia el temporizador para actualizaciones de la interfaz
    }
    
    /**
     * Inicializa los objetos del juego
     */
    private void initializeGameObjects() {
        playerPaddle = new Paddle(
            PADDLE_OFFSET, 
            PongGame.HEIGHT / 2 - PADDLE_HEIGHT / 2, 
            PADDLE_WIDTH, 
            PADDLE_HEIGHT, 
            currentTheme.getPaddleColor()
        );
        
        aiPaddle = new Paddle(
            PongGame.WIDTH - PADDLE_OFFSET - PADDLE_WIDTH, 
            PongGame.HEIGHT / 2 - PADDLE_HEIGHT / 2, 
            PADDLE_WIDTH, 
            PADDLE_HEIGHT, 
            currentTheme.getPaddleColor()
        );
        
        ball = new Ball(
            PongGame.WIDTH / 2, 
            PongGame.HEIGHT / 2, 
            BALL_SIZE
        );
        ball.setColor(currentTheme.getBallColor());
    }
    
    /**
     * Inicializa los componentes de la interfaz de usuario
     */
    private void initializeUI() {
        // Calcula el centrado vertical
        int totalButtonCount = 5; // Start, HowToPlay, Difficulty, Multiplayer, Theme
        int totalButtonsHeight = (BUTTON_HEIGHT * totalButtonCount) + (BUTTON_SPACING * (totalButtonCount - 1));
        int startY = (PongGame.HEIGHT - totalButtonsHeight) / 2;
        
        // Botón Iniciar Juego
        startButton = createButton("Iniciar Juego", startY, e -> {
            startGame();
            this.requestFocus();
        });
        
        // Botón Cómo Jugar
        howToPlayButton = createButton("Cómo Jugar", startY + BUTTON_HEIGHT + BUTTON_SPACING, e -> {
            setScreen(ScreenState.INSTRUCTIONS);
        });
        
        // Botón Dificultad
        difficultyButton = createButton("Nivel de Dificultad", startY + (BUTTON_HEIGHT + BUTTON_SPACING) * 2, e -> {
            setScreen(ScreenState.DIFFICULTY);
        });
        
        // Botón Multijugador
        multiplayerButton = createButton("Un Jugador", startY + (BUTTON_HEIGHT + BUTTON_SPACING) * 3, e -> {
            isMultiplayerMode = !isMultiplayerMode;
            multiplayerButton.setText(isMultiplayerMode ? "Modo Multijugador" : "Un Jugador");
            this.requestFocus();
        });
        
        // Botón Tema
        themeButton = createButton("Cambiar Tema", startY + (BUTTON_HEIGHT + BUTTON_SPACING) * 4, e -> {
            setScreen(ScreenState.THEMES);
        });
        
        // Botón Cerrar Instrucciones
        closeInstructionsButton = new ModernButton("X", true);
        closeInstructionsButton.setBounds(PongGame.WIDTH - 60, 20, 40, 40);
        closeInstructionsButton.setFocusable(false);
        closeInstructionsButton.addActionListener(e -> {
            setScreen(ScreenState.MAIN_MENU);
        });
        closeInstructionsButton.setVisible(false);
        add(closeInstructionsButton);
        
        // Inicializa botones de selección de dificultad
        initializeDifficultyButtons();
        
        // Inicializa botones de selección de tema
        initializeThemeButtons();
        
        // Aplica el tema inicial a todos los botones
        applyThemeToButtons(currentTheme);
        
        // Almacena las posiciones originales de los botones para la pantalla de fin de juego
        originalButtonPositions = new HashMap<>();
        originalButtonPositions.put(startButton, startButton.getY());
        originalButtonPositions.put(howToPlayButton, howToPlayButton.getY());
        originalButtonPositions.put(difficultyButton, difficultyButton.getY());
        originalButtonPositions.put(multiplayerButton, multiplayerButton.getY());
        originalButtonPositions.put(themeButton, themeButton.getY());
    }
    
    /**
     * Crea un botón con configuraciones comunes
     */
    private ModernButton createButton(String text, int y, ActionListener action) {
        ModernButton button = new ModernButton(text);
        button.setBounds(PongGame.WIDTH / 2 - BUTTON_WIDTH / 2, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setFocusable(false);
        button.addActionListener(action);
        add(button);
        return button;
    }
    
    /**
     * Crea un botón con descripción al pasar el ratón
     */
    private ModernButton createDescriptiveButton(String text, int y, String hoverDesc, ActionListener action) {
        ModernButton button = createButton(text, y, action);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                hoverDescription = hoverDesc;
                repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                hoverDescription = "";
                repaint();
            }
        });
        return button;
    }
    
    /**
     * Cambia el estado de pantalla actual
     */
    private void setScreen(ScreenState screen) {
        currentScreen = screen;        
        // Actualiza la interfaz según el estado
        closeInstructionsButton.setVisible(screen != ScreenState.MAIN_MENU && screen != ScreenState.GAME);
        this.requestFocus();
        repaint();
    }
    
    /**
     * Inicializa los botones de selección de dificultad
     */
    private void initializeDifficultyButtons() {
        // Botón Fácil
        easyButton = createDescriptiveButton("Fácil", 150, 
            "La IA se mueve lentamente y tiene tiempos de reacción largos,\n" +
            "permitiéndote anticipar y devolver la pelota con facilidad.\n" +
            "La velocidad de la pelota es estándar.",
            e -> {
                currentDifficulty = Difficulty.EASY;
                aiPaddleSpeed = 2;
                aiReactionTime = 0.8f;
                setScreen(ScreenState.MAIN_MENU);
            });
        
        // Botón Medio
        mediumButton = createDescriptiveButton("Medio", 210, 
            "La IA tiene mayor precisión y tiempos de reacción más cortos,\n" +
            "ofreciendo un desafío equilibrado sin aumentar la velocidad de la pelota.",
            e -> {
                currentDifficulty = Difficulty.MEDIUM;
                aiPaddleSpeed = 4;
                aiReactionTime = 0.5f;
                setScreen(ScreenState.MAIN_MENU);
            });
        
        // Botón Difícil
        hardButton = createDescriptiveButton("Difícil", 270, 
            "La IA anticipa mejor tus tiros y se mueve con gran agilidad;\n" +
            "además, la pelota viaja un poco más rápido para aumentar la intensidad.",
            e -> {
                currentDifficulty = Difficulty.HARD;
                aiPaddleSpeed = 5;
                aiReactionTime = 0.2f;
                setScreen(ScreenState.MAIN_MENU);
            });
        
        // Botón Volver para la pantalla de dificultad
        backButton = createButton("Volver", 380, e -> setScreen(ScreenState.MAIN_MENU));
    }
    
    /**
     * Inicializa los botones de selección de tema
     */
    private void initializeThemeButtons() {
        themeButtons = new JButton[Theme.AVAILABLE_THEMES.length];
        
        for (int i = 0; i < Theme.AVAILABLE_THEMES.length; i++) {
            Theme theme = Theme.AVAILABLE_THEMES[i];
            final int themeIndex = i;
            
            themeButtons[i] = createDescriptiveButton(theme.getName(), 150 + (i * 60),
                "Tema: " + theme.getName() + "\n" +
                "Cambia los colores de los elementos del juego.",
                e -> {
                    currentTheme = Theme.AVAILABLE_THEMES[themeIndex];
                    applyTheme(currentTheme);
                    setScreen(ScreenState.MAIN_MENU);
                });
        }
    }
    
    /**
     * Aplica el tema seleccionado a los elementos del juego
     * @param theme el tema a aplicar
     */
    private void applyTheme(Theme theme) {
        // Aplica tema al fondo
        setBackground(theme.getBackgroundColor());
        
        // Aplica tema a objetos del juego
        playerPaddle.setColor(theme.getPaddleColor());
        aiPaddle.setColor(theme.getPaddleColor());
        ball.setColor(theme.getBallColor());
        
        // Aplica tema a botones
        applyThemeToButtons(theme);
        
        // Almacena el tema actual
        currentTheme = theme;
    }
    
    /**
     * Aplica tema a todos los botones
     * @param theme el tema a aplicar
     */
    private void applyThemeToButtons(Theme theme) {
        // Usa iteración de componentes para encontrar todos los botones
        for (java.awt.Component component : getComponents()) {
            if (component instanceof ModernButton) {
                ((ModernButton)component).applyTheme(theme);
            }
        }
    }
    
    /**
     * Reposiciona los botones para la pantalla de fin de juego
     */
    private void repositionButtonsForGameOver() {
        int gameOverY = PongGame.HEIGHT / 2 + 30;
        int spacing = 55;
        
        startButton.setBounds(startButton.getX(), gameOverY, startButton.getWidth(), startButton.getHeight());
        howToPlayButton.setBounds(howToPlayButton.getX(), gameOverY + spacing, howToPlayButton.getWidth(), howToPlayButton.getHeight());
        difficultyButton.setBounds(difficultyButton.getX(), gameOverY + spacing * 2, difficultyButton.getWidth(), difficultyButton.getHeight());
        multiplayerButton.setBounds(multiplayerButton.getX(), gameOverY + spacing * 3, multiplayerButton.getWidth(), multiplayerButton.getHeight());
        themeButton.setBounds(themeButton.getX(), gameOverY + spacing * 4, themeButton.getWidth(), themeButton.getHeight());
    }
    
    /**
     * Dibuja todos los elementos del juego en la pantalla
     */
    private void draw(Graphics g) {
        // Dibuja el fondo (usando el tema actual)
        g.setColor(currentTheme.getBackgroundColor());
        g.fillRect(0, 0, PongGame.WIDTH, PongGame.HEIGHT);
        
        // Dibuja las paletas
        playerPaddle.draw(g);
        aiPaddle.draw(g);
        
        // Dibuja la pelota (solo si el juego está en ejecución y no ha terminado)
        if (gameRunning && !gameOver) {
            ball.draw(g);
        }
        
        // Dibuja la línea central divisoria
        g.setColor(currentTheme.getDividerColor());
        for (int i = 0; i < PongGame.HEIGHT; i += 50) {
            g.fillRect(PongGame.WIDTH / 2 - 1, i, 2, 25);
        }
        
        // Dibuja las puntuaciones con etiquetas apropiadas
        g.setColor(currentTheme.getTextColor());
        g.setFont(new Font("Arial", Font.BOLD, 30));
        
        if (isMultiplayerMode) {
            // En modo multijugador, muestra etiquetas P1 y P2
            g.drawString("P1: " + playerScore, PongGame.WIDTH / 2 - 80, 50);
            g.drawString("P2: " + aiScore, PongGame.WIDTH / 2 + 20, 50);
        } else {
            // En modo un jugador, muestra solo las puntuaciones
            g.drawString(String.valueOf(playerScore), PongGame.WIDTH / 2 - 50, 50);
            g.drawString(String.valueOf(aiScore), PongGame.WIDTH / 2 + 30, 50);
        }
        
        // Dibuja mensaje de fin de juego
        if (gameOver) {
            g.setColor(new Color(255, 215, 0, 220)); // Oro semitransparente
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("FIN DEL JUEGO", PongGame.WIDTH / 2 - 150, PongGame.HEIGHT / 2 - 50);
            
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString(winner + " Gana!", PongGame.WIDTH / 2 - 70, PongGame.HEIGHT / 2);
            
            // Solo muestra instrucción de teclado si los botones no son visibles
            if (!startButton.isVisible()) {
                g.setFont(new Font("Arial", Font.PLAIN, 20));
                g.drawString("Presiona ESPACIO para jugar de nuevo", PongGame.WIDTH / 2 - 170, PongGame.HEIGHT / 2 + 40);
            }
        }
        // Dibuja mensaje de pausa si el juego está en pausa y no ha terminado
        else if (gamePaused && gameRunning) {
            g.setColor(new Color(255, 255, 255, 200)); // Blanco semitransparente
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("PAUSA", PongGame.WIDTH / 2 - 100, PongGame.HEIGHT / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Presiona ESPACIO para continuar", PongGame.WIDTH / 2 - 140, PongGame.HEIGHT / 2 + 40);
        }
        
        // Muestra el modo de juego y tema en la esquina
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.setColor(currentTheme.getTextColor());
        g.drawString(isMultiplayerMode ? "Modo Multijugador" : "Un Jugador", 10, PongGame.HEIGHT - 25);
        g.drawString("Tema: " + currentTheme.getName(), 10, PongGame.HEIGHT - 10);
    }
    
    /**
     * Dibuja el overlay de instrucciones de juego
     */
    private void drawInstructions(Graphics g) {
        // Dibuja fondo semitransparente
        g.setColor(currentTheme.getPanelOverlayColor());
        g.fillRect(0, 0, PongGame.WIDTH, PongGame.HEIGHT);
        
        // Dibuja título de instrucciones
        g.setColor(currentTheme.getTextColor());
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Cómo Jugar", PongGame.WIDTH / 2 - 120, 80);
        
        // Dibuja texto de instrucciones
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String[] instructions;
        
        if (isMultiplayerMode) {
            instructions = new String[] {
                "• Jugador 1: Usa la tecla W para mover la paleta hacia arriba",
                "• Jugador 1: Usa la tecla S para mover la paleta hacia abajo",
                "• Jugador 2: Usa la FLECHA ARRIBA para mover la paleta hacia arriba", 
                "• Jugador 2: Usa la FLECHA ABAJO para mover la paleta hacia abajo",
                "• Anota puntos haciendo que la pelota pase la paleta del oponente",
                "• La pelota rebotará en diferentes ángulos dependiendo",
                "  de dónde golpee tu paleta",
                "• Presiona ESPACIO para pausar/reanudar el juego",
                "• El primer jugador en llegar a 10 puntos gana!"
            };
        } else {
            instructions = new String[] {
                "• Usa la tecla W para mover la paleta hacia arriba",
                "• Usa la tecla S para mover la paleta hacia abajo",
                "• Anota puntos haciendo que la pelota pase la paleta de la IA",
                "• La pelota rebotará en diferentes ángulos dependiendo",
                "  de dónde golpee tu paleta",
                "• Presiona ESPACIO para pausar/reanudar el juego",
                "• El primer jugador en llegar a 10 puntos gana!"
            };
        }
        
        int yPos = 150;
        for (String line : instructions) {
            g.drawString(line, PongGame.WIDTH / 2 - 200, yPos);
            yPos += 40;
        }
        
        // El botón Cerrar (X) se maneja por separado como un JButton
    }
    
    /**
     * Dibuja la pantalla de selección de dificultad
     */
    private void drawDifficulty(Graphics g) {
        // Dibuja fondo semitransparente
        g.setColor(currentTheme.getPanelOverlayColor());
        g.fillRect(0, 0, PongGame.WIDTH, PongGame.HEIGHT);
        
        // Dibuja título
        g.setColor(currentTheme.getTextColor());
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Selecciona Dificultad", PongGame.WIDTH / 2 - 180, 80);
        
        // Dibuja dificultad actual
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String difficultyName;
        switch (currentDifficulty) {
            case EASY: difficultyName = "Fácil"; break;
            case MEDIUM: difficultyName = "Medio"; break;
            case HARD: difficultyName = "Difícil"; break;
            default: difficultyName = currentDifficulty.toString(); break;
        }
        g.drawString("Actual: " + difficultyName, PongGame.WIDTH / 2 - 80, 120);
        
        // Dibuja descripción al pasar el ratón, si hay alguna
        if (!hoverDescription.isEmpty()) {
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            drawMultiLineText(g, hoverDescription, PongGame.WIDTH / 2 - 220, 330);
        }
        
        // Muestra nota de que la dificultad solo se aplica a un jugador
        if (isMultiplayerMode) {
            g.setFont(new Font("Arial", Font.ITALIC, 16));
            g.setColor(Color.YELLOW);
            g.drawString("Nota: La configuración de dificultad solo aplica en modo un jugador", 
                         PongGame.WIDTH / 2 - 250, 430);
        }
    }
    
    /**
     * Dibuja la pantalla de selección de tema
     */
    private void drawThemes(Graphics g) {
        // Dibuja fondo semitransparente
        g.setColor(currentTheme.getPanelOverlayColor());
        g.fillRect(0, 0, PongGame.WIDTH, PongGame.HEIGHT);
        
        // Dibuja título
        g.setColor(currentTheme.getTextColor());
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Seleccionar Tema", PongGame.WIDTH / 2 - 140, 80);
        
        // Dibuja tema actual
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Actual: " + currentTheme.getName(), PongGame.WIDTH / 2 - 80, 120);
        
        // Dibuja descripción al pasar el ratón, si hay alguna
        if (!hoverDescription.isEmpty()) {
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            drawMultiLineText(g, hoverDescription, PongGame.WIDTH / 2 - 150, 380);
        }
    }

    /**
     * Método auxiliar para dibujar texto multilínea
     */
    private void drawMultiLineText(Graphics g, String text, int x, int y) {
        String[] lines = text.split("\n");
        int lineHeight = g.getFontMetrics().getHeight();
        
        for (String line : lines) {
            g.drawString(line, x, y);
            y += lineHeight;
        }
    }

    /**
     * Actualiza el estado del juego para cada fotograma
     */
    private void update() {
        // No hacer nada si el juego está pausado
        if (gamePaused) return;
        
        // Actualizar posición de la paleta del jugador siempre
        playerPaddle.update();
        
        // Si el juego no está en curso o ha terminado, no actualizar más elementos
        if (!gameRunning || gameOver) {
            if (gameOver) {
                aiPaddle.setYVelocity(0);
                aiPaddle.update();
            }
            return;
        }
        
        // Actualizar la paleta del oponente
        if (isMultiplayerMode) {
            aiPaddle.update();
        } else {
            updateAIPaddle();
        }
        
        // Ajustar velocidad de la pelota según dificultad
        updateBallSpeed();
        
        // Actualizar elementos del juego
        ball.update();
        checkCollision();
        checkScoring();
    }
    
    /**
     * Actualiza la velocidad de la pelota según la dificultad actual
     */
    private void updateBallSpeed() {
        if (currentDifficulty == Difficulty.HARD && !isMultiplayerMode) {
            // La dificultad difícil tiene pelota más rápida (solo en un jugador)
            ball.setSpeedMultiplier(1.5f);  // Aumentado de 1.2f
        } else if (currentDifficulty == Difficulty.MEDIUM && !isMultiplayerMode) {
            // La dificultad media tiene pelota un poco más rápida
            ball.setSpeedMultiplier(1.2f);  // Velocidad media
        } else {
            // Velocidad normal para Fácil y multijugador
            ball.setSpeedMultiplier(1.1f);  // Ligeramente más rápido que antes
        }
    }

    /**
     * Actualiza el movimiento de la paleta de la IA según la dificultad actual
     */
    private void updateAIPaddle() {
        // Convertir a int para manejar valores double devueltos
        int aiPaddleCenterY = (int)(aiPaddle.getY() + aiPaddle.getHeight() / 2);
        int ballCenterY = (int)(ball.getY() + ball.getHeight() / 2);
        
        // Añade retardo de reacción basado en la dificultad
        // Para dificultades más altas, la IA predice dónde estará la pelota
        int targetY = ballCenterY;
        
        if (currentDifficulty == Difficulty.HARD) {
            // Dificultad difícil: la IA intenta predecir la trayectoria de la pelota
            if (ball.getXVelocity() > 0) { // Pelota moviéndose hacia la IA
                // Predicción de trayectoria simple
                float ballDistanceToAI = (float)(aiPaddle.getX() - ball.getX());
                float timeToReachAI = ballDistanceToAI / ball.getXVelocity();
                int predictedY = (int)(ball.getY() + (ball.getYVelocity() * timeToReachAI));
                
                // Usa predicción con algo de margen de error
                targetY = predictedY + (int)(ball.getHeight() / 2);
                
                // Asegúrate de que la predicción esté dentro de los límites
                targetY = Math.max(targetY, 0);
                targetY = Math.min(targetY, PongGame.HEIGHT - (int)aiPaddle.getHeight());
            }
        }
        
        // Aplica "tiempo de reacción" - hace que la IA sea menos perfecta en dificultades más fáciles
        double reactionChance = Math.random();
        if (reactionChance > aiReactionTime) {
            if (aiPaddleCenterY < targetY) {
                aiPaddle.setYVelocity(aiPaddleSpeed);
            } else if (aiPaddleCenterY > targetY) {
                aiPaddle.setYVelocity(-aiPaddleSpeed);
            } else {
                aiPaddle.setYVelocity(0);
            }
        }
        
        aiPaddle.update();
    }
    
    /**
     * Comprueba y maneja colisiones de la pelota con las paletas
     */
    private void checkCollision() {
        // Colisión de la pelota con las paletas
        if (playerPaddle.intersects(ball)) {
            // Usa el nuevo método de deflexión en lugar de inversión simple
            ball.deflectFromPaddle(playerPaddle);
        }
        
        if (aiPaddle.intersects(ball)) {
            // Usa el nuevo método de deflexión en lugar de inversión simple
            ball.deflectFromPaddle(aiPaddle);
        }
    }
    
    /**
     * Comprueba si un jugador ha anotado
     */
    private void checkScoring() {
        // El jugador anota (la pelota pasa la paleta de la IA)
        if (ball.getX() + ball.getWidth() >= PongGame.WIDTH) {
            playerScore++;
            ball.reset();
            
            // Comprueba condición de victoria
            if (playerScore >= WINNING_SCORE) {
                gameOver = true;
                winner = isMultiplayerMode ? "Jugador 1" : "Jugador";
            }
        }
        
        // La IA anota (la pelota pasa la paleta del jugador)
        if (ball.getX() <= 0) {
            aiScore++;
            ball.reset();
            
            // Comprueba condición de victoria
            if (aiScore >= WINNING_SCORE) {
                gameOver = true;
                winner = isMultiplayerMode ? "Jugador 2" : "IA";
            }
        }
    }
    
    /**
     * Inicia un nuevo juego
     */
    private void startGame() {
        // Reinicia la pelota
        ball.reset();
        
        // Reinicia las paletas a posiciones centrales
        playerPaddle.reset();
        aiPaddle.reset();
        
        // Reinicia el estado del juego
        playerScore = 0;
        aiScore = 0;
        gameRunning = true;
        gameOver = false;
        winner = "";
        gamePaused = false;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
        
        // Mostrar/ocultar botones según estado del juego y visibilidad del menú
        boolean showMainButtons = (!gameRunning || gameOver) && 
                                  currentScreen == ScreenState.MAIN_MENU;
        startButton.setVisible(showMainButtons);
        howToPlayButton.setVisible(showMainButtons);
        difficultyButton.setVisible(showMainButtons);
        multiplayerButton.setVisible(showMainButtons);
        themeButton.setVisible(showMainButtons);
        
        // Si el juego terminó, reposiciona los botones a sus posiciones originales
        if (gameOver && showMainButtons) {
            repositionButtonsForGameOver();
        }
        
        // Botones de instrucciones y cerrar
        closeInstructionsButton.setVisible(currentScreen != ScreenState.MAIN_MENU && 
                                         currentScreen != ScreenState.GAME);
        
        // Botones de dificultad
        easyButton.setVisible(currentScreen == ScreenState.DIFFICULTY);
        mediumButton.setVisible(currentScreen == ScreenState.DIFFICULTY);
        hardButton.setVisible(currentScreen == ScreenState.DIFFICULTY);
        backButton.setVisible(currentScreen == ScreenState.DIFFICULTY);
        
        // Botones de tema
        if (themeButtons != null) {
            for (JButton button : themeButtons) {
                if (button != null) {
                    button.setVisible(currentScreen == ScreenState.THEMES);
                }
            }
        }
        
        // Cambia texto del botón según el estado del juego
        if (gameOver) {
            startButton.setText("¿Jugar de nuevo?");
        } else if (!gameRunning) {
            startButton.setText("Iniciar Juego");
        }
        
        // Dibuja overlays si es necesario
        switch (currentScreen) {
            case INSTRUCTIONS: drawInstructions(g); break;
            case DIFFICULTY: drawDifficulty(g); break;
            case THEMES: drawThemes(g); break;
            default: break;
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_W && !gamePaused && !gameOver) {
            playerPaddle.setYVelocity(-PADDLE_SPEED);
        }
        if (key == KeyEvent.VK_S && !gamePaused && !gameOver) {
            playerPaddle.setYVelocity(PADDLE_SPEED);
        }
        
        // Controles del Jugador 2 en modo multijugador
        if (isMultiplayerMode && !gamePaused && !gameOver) {
            if (key == KeyEvent.VK_UP) {
                aiPaddle.setYVelocity(-PADDLE_SPEED);
            }
            if (key == KeyEvent.VK_DOWN) {
                aiPaddle.setYVelocity(PADDLE_SPEED);
            }
        }
        
        if (key == KeyEvent.VK_SPACE) {
            if (gameOver || !gameRunning) {
                startGame();
            } else {
                // Alterna estado de pausa
                gamePaused = !gamePaused;
            }
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_W || key == KeyEvent.VK_S) {
            playerPaddle.setYVelocity(0);
        }
        
        // Liberación de teclas del Jugador 2 en modo multijugador
        if (isMultiplayerMode) {
            if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) {
                aiPaddle.setYVelocity(0);
            }
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // No se utiliza
    }
}