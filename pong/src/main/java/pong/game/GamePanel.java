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
 * Panel that contains the actual game elements and logic.
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener {
    // Game objects
    private Paddle playerPaddle;
    private Paddle aiPaddle;
    private Ball ball;
    
    // UI components
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
    private boolean showingInstructions = false;
    private boolean showingDifficulty = false;
    private boolean showingThemes = false;
    private String hoverDescription = "";
    
    // Game state
    private int playerScore = 0;
    private int aiScore = 0;
    private boolean gameRunning = false;
    private boolean gamePaused = false;
    private boolean gameOver = false;
    private boolean isMultiplayerMode = false;
    private String winner = "";
    private final int WINNING_SCORE = 10;
    
    // Difficulty settings
    public enum Difficulty {EASY, MEDIUM, HARD}
    private Difficulty currentDifficulty = Difficulty.MEDIUM;
    private int aiPaddleSpeed = 4; // Default for MEDIUM
    private float aiReactionTime = 0.5f; // Default for MEDIUM
    
    // Theme settings
    private Theme currentTheme = Theme.CLASSIC;
    private JButton[] themeButtons;
    
    // Timer for game loop
    private Timer gameTimer;
    private final int DELAY = 10;
    private final int PADDLE_SPEED = 6;

    private Map<JButton, Integer> originalButtonPositions;
    
    /**
     * Creates and initializes the game panel
     */
    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        setLayout(null); // Use absolute positioning
        
        // Initialize UI components
        initializeUI();
        
        // Initialize game objects
        int paddleHeight = 80;
        playerPaddle = new Paddle(30, PongGame.HEIGHT / 2 - paddleHeight / 2, 
                                  15, paddleHeight, currentTheme.getPaddleColor());
        aiPaddle = new Paddle(PongGame.WIDTH - 45, PongGame.HEIGHT / 2 - paddleHeight / 2, 
                             15, paddleHeight, currentTheme.getPaddleColor());
        ball = new Ball(PongGame.WIDTH / 2, PongGame.HEIGHT / 2, 15);
        ball.setColor(currentTheme.getBallColor());
        
        // Set up game timer
        gameTimer = new Timer(DELAY, this);
        gameTimer.start(); // Start the timer right away for UI updates
    }
    
    /**
     * Initializes UI components
     */
    private void initializeUI() {
    // Calculate vertical centering
    int buttonHeight = 45;
    int buttonSpacing = 10;
    int totalButtonCount = 5; // Start, HowToPlay, Difficulty, Multiplayer, Theme
    int totalButtonsHeight = (buttonHeight * totalButtonCount) + (buttonSpacing * (totalButtonCount - 1));
    int startY = (PongGame.HEIGHT - totalButtonsHeight) / 2;
    
    // Start Game button
    startButton = new ModernButton("Start Game");
    startButton.setBounds(PongGame.WIDTH / 2 - 100, startY, 200, buttonHeight);
    startButton.setFocusable(false);
    startButton.addActionListener(e -> {
        startGame();
        this.requestFocus();
    });
    add(startButton);
    
    // How To Play button
    howToPlayButton = new ModernButton("How To Play");
    howToPlayButton.setBounds(PongGame.WIDTH / 2 - 100, startY + buttonHeight + buttonSpacing, 200, buttonHeight);
    howToPlayButton.setFocusable(false);
    howToPlayButton.addActionListener(e -> {
        showingInstructions = true;
        showingDifficulty = false;
        showingThemes = false;
        this.requestFocus();
        repaint();
        closeInstructionsButton.setVisible(true);
    });
    add(howToPlayButton);
    
    // Difficulty button
    difficultyButton = new ModernButton("Difficulty Level");
    difficultyButton.setBounds(PongGame.WIDTH / 2 - 100, startY + (buttonHeight + buttonSpacing) * 2, 200, buttonHeight);
    difficultyButton.setFocusable(false);
    difficultyButton.addActionListener(e -> {
        showingDifficulty = true;
        showingInstructions = false;
        showingThemes = false;
        this.requestFocus();
        repaint();
    });
    add(difficultyButton);
    
    // Multiplayer toggle button
    multiplayerButton = new ModernButton("Single Player");
    multiplayerButton.setBounds(PongGame.WIDTH / 2 - 100, startY + (buttonHeight + buttonSpacing) * 3, 200, buttonHeight);
    multiplayerButton.setFocusable(false);
    multiplayerButton.addActionListener(e -> {
        isMultiplayerMode = !isMultiplayerMode;
        multiplayerButton.setText(isMultiplayerMode ? "Multiplayer Mode" : "Single Player");
        this.requestFocus();
    });
    add(multiplayerButton);
    
    // Theme button
    themeButton = new ModernButton("Change Theme");
    themeButton.setBounds(PongGame.WIDTH / 2 - 100, startY + (buttonHeight + buttonSpacing) * 4, 200, buttonHeight);
    themeButton.setFocusable(false);
    themeButton.addActionListener(e -> {
        showingThemes = true;
        showingInstructions = false;
        showingDifficulty = false;
        this.requestFocus();
        repaint();
        closeInstructionsButton.setVisible(true);
    });
    add(themeButton);
    
    // Close Instructions button
    closeInstructionsButton = new ModernButton("X", true);
    closeInstructionsButton.setBounds(PongGame.WIDTH - 60, 20, 40, 40);
    closeInstructionsButton.setFocusable(false);
    closeInstructionsButton.addActionListener(e -> {
        showingInstructions = false;
        showingDifficulty = false;
        showingThemes = false;
        this.requestFocus();
        repaint();
    });
    closeInstructionsButton.setVisible(false);
    add(closeInstructionsButton);
    
    // Initialize difficulty selection buttons
    initializeDifficultyButtons();
    
    // Initialize theme selection buttons
    initializeThemeButtons();
    
    // Apply initial theme to all buttons
    applyThemeToButtons(currentTheme);
    
    // Store the original button positions for game over screen
    originalButtonPositions = new HashMap<>();
    originalButtonPositions.put(startButton, startButton.getY());
    originalButtonPositions.put(howToPlayButton, howToPlayButton.getY());
    originalButtonPositions.put(difficultyButton, difficultyButton.getY());
    originalButtonPositions.put(multiplayerButton, multiplayerButton.getY());
    originalButtonPositions.put(themeButton, themeButton.getY());
    }

    /**
     * Initializes difficulty selection buttons
     */
    private void initializeDifficultyButtons() {
        // Easy button
        easyButton = new ModernButton("Easy");
        easyButton.setBounds(PongGame.WIDTH / 2 - 100, 150, 200, 45);
        easyButton.setFocusable(false);
        easyButton.addActionListener(e -> {
            currentDifficulty = Difficulty.EASY;
            aiPaddleSpeed = 2;
            aiReactionTime = 0.8f;
            showingDifficulty = false;
            this.requestFocus();
            repaint();
        });
        easyButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                hoverDescription = "The AI moves slowly and has long reaction times, letting you\n" +
                                "anticipate and return the ball with ease. Ball speed is standard.";
                repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                hoverDescription = "";
                repaint();
            }
        });
        add(easyButton);
        
        // Medium button
        mediumButton = new ModernButton("Medium");
        mediumButton.setBounds(PongGame.WIDTH / 2 - 100, 210, 200, 45);
        mediumButton.setFocusable(false);
        mediumButton.addActionListener(e -> {
            currentDifficulty = Difficulty.MEDIUM;
            aiPaddleSpeed = 4;
            aiReactionTime = 0.5f;
            showingDifficulty = false;
            this.requestFocus();
            repaint();
        });
        mediumButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                hoverDescription = "The AI has improved accuracy and shorter reaction times,\n" +
                                "offering a balanced challenge without increasing ball speed.";
                repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                hoverDescription = "";
                repaint();
            }
        });
        add(mediumButton);
        
        // Hard button
        hardButton = new ModernButton("Hard");
        hardButton.setBounds(PongGame.WIDTH / 2 - 100, 270, 200, 45);
        hardButton.setFocusable(false);
        hardButton.addActionListener(e -> {
            currentDifficulty = Difficulty.HARD;
            aiPaddleSpeed = 5;
            aiReactionTime = 0.2f;
            showingDifficulty = false;
            this.requestFocus();
            repaint();
        });
        hardButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                hoverDescription = "The AI anticipates your shots better and moves with great agility;\n" +
                                "additionally, the ball travels slightly faster to amp up the intensity.";
                repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                hoverDescription = "";
                repaint();
            }
        });
        add(hardButton);
        
        // Back button for difficulty screen
        backButton = new ModernButton("Back");
        backButton.setBounds(PongGame.WIDTH / 2 - 100, 380, 200, 45);
        backButton.setFocusable(false);
        backButton.addActionListener(e -> {
            showingDifficulty = false;
            this.requestFocus();
            repaint();
        });
        add(backButton);
    }
    
    /**
     * Initializes theme selection buttons
     */
    private void initializeThemeButtons() {
        themeButtons = new JButton[Theme.AVAILABLE_THEMES.length];
        
        for (int i = 0; i < Theme.AVAILABLE_THEMES.length; i++) {
            Theme theme = Theme.AVAILABLE_THEMES[i];
            themeButtons[i] = new ModernButton(theme.getName());
            themeButtons[i].setBounds(PongGame.WIDTH / 2 - 100, 150 + (i * 60), 200, 45);
            themeButtons[i].setFocusable(false);
            
            // Store the theme index for the action listener
            final int themeIndex = i;
            themeButtons[i].addActionListener(e -> {
                currentTheme = Theme.AVAILABLE_THEMES[themeIndex];
                applyTheme(currentTheme);
                showingThemes = false;
                this.requestFocus();
                repaint();
            });
            
            themeButtons[i].addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    hoverDescription = "Theme: " + theme.getName() + "\n" +
                                    "Changes the colors of the game elements.";
                    repaint();
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    hoverDescription = "";
                    repaint();
                }
            });
            
            add(themeButtons[i]);
        }
    }
    
    /**
     * Applies the selected theme to the game elements
     * @param theme the theme to apply
     */
    private void applyTheme(Theme theme) {
        // Apply theme to background
        setBackground(theme.getBackgroundColor());
        
        // Apply theme to game objects
        playerPaddle.setColor(theme.getPaddleColor());
        aiPaddle.setColor(theme.getPaddleColor());
        ball.setColor(theme.getBallColor());
        
        // Apply theme to buttons
        applyThemeToButtons(theme);
        
        // Store the current theme
        currentTheme = theme;
    }
    
    /**
     * Applies theme to all buttons
     * @param theme the theme to apply
     */
    private void applyThemeToButtons(Theme theme) {
        // Apply theme to ModernButtons
        ModernButton[] buttons = {
            (ModernButton)startButton, (ModernButton)howToPlayButton, 
            (ModernButton)difficultyButton, (ModernButton)multiplayerButton,
            (ModernButton)themeButton, (ModernButton)closeInstructionsButton,
            (ModernButton)easyButton, (ModernButton)mediumButton,
            (ModernButton)hardButton, (ModernButton)backButton
        };
        
        for (ModernButton button : buttons) {
            if (button != null) {
                button.applyTheme(theme);
            }
        }
        
        // Apply theme to theme selection buttons if initialized
        if (themeButtons != null) {
            for (JButton button : themeButtons) {
                if (button != null && button instanceof ModernButton) {
                    ((ModernButton)button).applyTheme(theme);
                }
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
        
        // Show/hide buttons based on game state and menu visibility
        boolean showMainButtons = (!gameRunning || gameOver) && !showingInstructions && 
                                !showingDifficulty && !showingThemes;
        startButton.setVisible(showMainButtons);
        howToPlayButton.setVisible(showMainButtons);
        difficultyButton.setVisible(showMainButtons);
        multiplayerButton.setVisible(showMainButtons);
        themeButton.setVisible(showMainButtons);
        
        // If game over, reposition buttons to their original positions
        if (gameOver && showMainButtons) {
            repositionButtonsForGameOver();
        }
        
        // Instructions and close button
        closeInstructionsButton.setVisible(showingInstructions || showingDifficulty || showingThemes);
        
        // Difficulty buttons
        easyButton.setVisible(showingDifficulty);
        mediumButton.setVisible(showingDifficulty);
        hardButton.setVisible(showingDifficulty);
        backButton.setVisible(showingDifficulty);
        
        // Theme buttons
        if (themeButtons != null) {
            for (JButton button : themeButtons) {
                if (button != null) {
                    button.setVisible(showingThemes);
                }
            }
        }
        
        // Change button text based on game state
        if (gameOver) {
            startButton.setText("Play Again?");
        } else if (!gameRunning) {
            startButton.setText("Start Game");
        }
        
        // Draw overlays if needed
        if (showingInstructions) {
            drawInstructions(g);
        } else if (showingDifficulty) {
            drawDifficulty(g);
        } else if (showingThemes) {
            drawThemes(g);
        }
    }

    /**
     * Repositions buttons for game over screen to fixed positions
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
     * Draws all game elements on the screen
     */
    private void draw(Graphics g) {
        // Draw background (using current theme)
        g.setColor(currentTheme.getBackgroundColor());
        g.fillRect(0, 0, PongGame.WIDTH, PongGame.HEIGHT);
        
        // Draw paddles
        playerPaddle.draw(g);
        aiPaddle.draw(g);
        
        // Draw ball (only if game is running and not over)
        if (gameRunning && !gameOver) {
            ball.draw(g);
        }
        
        // Draw center dividing line
        g.setColor(currentTheme.getDividerColor());
        for (int i = 0; i < PongGame.HEIGHT; i += 50) {
            g.fillRect(PongGame.WIDTH / 2 - 1, i, 2, 25);
        }
        
        // Draw scores with appropriate labels
        g.setColor(currentTheme.getTextColor());
        g.setFont(new Font("Arial", Font.BOLD, 30));
        
        if (isMultiplayerMode) {
            // In multiplayer mode, show P1 and P2 labels
            g.drawString("P1: " + playerScore, PongGame.WIDTH / 2 - 80, 50);
            g.drawString("P2: " + aiScore, PongGame.WIDTH / 2 + 20, 50);
        } else {
            // In single player mode, show just the scores
            g.drawString(String.valueOf(playerScore), PongGame.WIDTH / 2 - 50, 50);
            g.drawString(String.valueOf(aiScore), PongGame.WIDTH / 2 + 30, 50);
        }
        
        // Draw game over message
        if (gameOver) {
            g.setColor(new Color(255, 215, 0, 220)); // Semi-transparent gold
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("GAME OVER", PongGame.WIDTH / 2 - 150, PongGame.HEIGHT / 2 - 50);
            
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString(winner + " Wins!", PongGame.WIDTH / 2 - 70, PongGame.HEIGHT / 2);
            
            // Only show keyboard instruction if buttons aren't visible
            if (!startButton.isVisible()) {
                g.setFont(new Font("Arial", Font.PLAIN, 20));
                g.drawString("Press SPACE to play again", PongGame.WIDTH / 2 - 110, PongGame.HEIGHT / 2 + 40);
            }
        }
        // Draw pause message if game is paused and not over
        else if (gamePaused && gameRunning) {
            g.setColor(new Color(255, 255, 255, 200)); // Semi-transparent white
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("PAUSED", PongGame.WIDTH / 2 - 100, PongGame.HEIGHT / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press SPACE to continue", PongGame.WIDTH / 2 - 110, PongGame.HEIGHT / 2 + 40);
        }
        
        // Display game mode and theme in corner
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.setColor(currentTheme.getTextColor());
        g.drawString(isMultiplayerMode ? "Multiplayer Mode" : "Single Player", 10, PongGame.HEIGHT - 25);
        g.drawString("Theme: " + currentTheme.getName(), 10, PongGame.HEIGHT - 10);
    }

     /**
     * Draws the how-to-play instructions overlay
     */
    private void drawInstructions(Graphics g) {
        // Draw semi-transparent background
        g.setColor(currentTheme.getPanelOverlayColor());
        g.fillRect(0, 0, PongGame.WIDTH, PongGame.HEIGHT);
        
        // Draw instructions title
        g.setColor(currentTheme.getTextColor());
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("How To Play", PongGame.WIDTH / 2 - 120, 80);
        
        // Draw instructions text
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String[] instructions;
        
        if (isMultiplayerMode) {
            instructions = new String[] {
                "• Player 1: Use W key to move paddle up",
                "• Player 1: Use S key to move paddle down",
                "• Player 2: Use UP ARROW to move paddle up", 
                "• Player 2: Use DOWN ARROW to move paddle down",
                "• Score points by getting the ball past the opponent's paddle",
                "• The ball will bounce at different angles depending",
                "  on where it hits your paddle",
                "• Press SPACE to pause/resume the game",
                "• First player to reach 10 points wins!"
            };
        } else {
            instructions = new String[] {
                "• Use W key to move paddle up",
                "• Use S key to move paddle down",
                "• Score points by getting the ball past the AI paddle",
                "• The ball will bounce at different angles depending",
                "  on where it hits your paddle",
                "• Press SPACE to pause/resume the game",
                "• First player to reach 10 points wins!"
            };
        }
        
        int yPos = 150;
        for (String line : instructions) {
            g.drawString(line, PongGame.WIDTH / 2 - 200, yPos);
            yPos += 40;
        }
        
        // The Close (X) button is handled separately as a JButton
    }
    
    /**
     * Draws the difficulty selection screen
     */
    private void drawDifficulty(Graphics g) {
        // Draw semi-transparent background
        g.setColor(currentTheme.getPanelOverlayColor());
        g.fillRect(0, 0, PongGame.WIDTH, PongGame.HEIGHT);
        
        // Draw title
        g.setColor(currentTheme.getTextColor());
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Select Difficulty", PongGame.WIDTH / 2 - 140, 80);
        
        // Draw current difficulty
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Current: " + currentDifficulty.toString(), PongGame.WIDTH / 2 - 80, 120);
        
        // Draw hover description if any
        if (!hoverDescription.isEmpty()) {
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            drawMultiLineText(g, hoverDescription, PongGame.WIDTH / 2 - 220, 330);
        }
        
        // Show note that difficulty only applies to single player
        if (isMultiplayerMode) {
            g.setFont(new Font("Arial", Font.ITALIC, 16));
            g.setColor(Color.YELLOW);
            g.drawString("Note: Difficulty settings only apply in single player mode", 
                         PongGame.WIDTH / 2 - 220, 430);
        }
    }
    
    /**
     * Draws the theme selection screen
     */
    private void drawThemes(Graphics g) {
        // Draw semi-transparent background
        g.setColor(currentTheme.getPanelOverlayColor());
        g.fillRect(0, 0, PongGame.WIDTH, PongGame.HEIGHT);
        
        // Draw title
        g.setColor(currentTheme.getTextColor());
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Select Theme", PongGame.WIDTH / 2 - 120, 80);
        
        // Draw current theme
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Current: " + currentTheme.getName(), PongGame.WIDTH / 2 - 80, 120);
        
        // Draw hover description if any
        if (!hoverDescription.isEmpty()) {
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            drawMultiLineText(g, hoverDescription, PongGame.WIDTH / 2 - 150, 380);
        }
    }

    /**
     * Helper method to draw multi-line text
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
     * Updates the game state for each frame
     */
    private void update() {
        // Only allow paddle movement when game is not paused
        if (!gamePaused) {
            // Update paddle positions regardless of game state
            playerPaddle.update();
            
            // Only update AI, ball and check scoring if game is running AND not over
            if (gameRunning && !gameOver) {
                // In multiplayer mode, don't apply AI logic - the second paddle is controlled by arrow keys
                if (!isMultiplayerMode) {
                    // AI paddle movement with difficulty-based behavior
                    updateAIPaddle();
                } else {
                    // Just update the position of the "AI" paddle in multiplayer mode
                    aiPaddle.update();
                }
                
                // Update ball speed based on difficulty
                // Update ball speed based on difficulty
                if (currentDifficulty == Difficulty.HARD && !isMultiplayerMode) {
                    // Hard difficulty has faster ball (only in single player)
                    ball.setSpeedMultiplier(1.5f);  // Increased from 1.2f
                } else if (currentDifficulty == Difficulty.MEDIUM && !isMultiplayerMode) {
                    // Medium difficulty has slightly faster ball
                    ball.setSpeedMultiplier(1.2f);  // Medium speed
                } else {
                    // Normal ball speed for Easy and multiplayer
                    ball.setSpeedMultiplier(1.1f);  // Slightly faster than before
                }
                
                // Update ball
                ball.update();
                
                // Check collisions
                checkCollision();
                
                // Check for scoring
                checkScoring();
            }
            // If game is over, make sure AI paddle stops moving
            else if (gameOver) {
                aiPaddle.setYVelocity(0);
                aiPaddle.update();
            }
        }
    }

    /**
     * Updates AI paddle movement based on current difficulty
     */
    private void updateAIPaddle() {
        // Cast to int to handle double return values
        int aiPaddleCenterY = (int)(aiPaddle.getY() + aiPaddle.getHeight() / 2);
        int ballCenterY = (int)(ball.getY() + ball.getHeight() / 2);
        
        // Add reaction delay based on difficulty
        // For harder difficulties, AI predicts where the ball will be
        int targetY = ballCenterY;
        
        if (currentDifficulty == Difficulty.HARD) {
            // Hard difficulty: AI tries to predict ball trajectory
            if (ball.getXVelocity() > 0) { // Ball moving toward AI
                // Simple trajectory prediction
                float ballDistanceToAI = (float)(aiPaddle.getX() - ball.getX());
                float timeToReachAI = ballDistanceToAI / ball.getXVelocity();
                int predictedY = (int)(ball.getY() + (ball.getYVelocity() * timeToReachAI));
                
                // Use prediction with some error margin
                targetY = predictedY + (int)(ball.getHeight() / 2);
                
                // Ensure prediction is within bounds
                targetY = Math.max(targetY, 0);
                targetY = Math.min(targetY, PongGame.HEIGHT - (int)aiPaddle.getHeight());
            }
        }
        
        // Apply "reaction time" - make AI less perfect on easier difficulties
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
     * Checks and handles ball collisions with paddles
     */
    private void checkCollision() {
        // Ball collision with paddles
        if (playerPaddle.intersects(ball)) {
            // Use the new deflection method instead of simple reversal
            ball.deflectFromPaddle(playerPaddle);
        }
        
        if (aiPaddle.intersects(ball)) {
            // Use the new deflection method instead of simple reversal
            ball.deflectFromPaddle(aiPaddle);
        }
    }
    
    /**
     * Checks if a player has scored
     */
    private void checkScoring() {
        // Player scores (ball goes past AI paddle)
        if (ball.getX() + ball.getWidth() >= PongGame.WIDTH) {
            playerScore++;
            ball.reset();
            
            // Check for win condition
            if (playerScore >= WINNING_SCORE) {
                gameOver = true;
                winner = isMultiplayerMode ? "Player 1" : "Player";
            }
        }
        
        // AI scores (ball goes past player paddle)
        if (ball.getX() <= 0) {
            aiScore++;
            ball.reset();
            
            // Check for win condition
            if (aiScore >= WINNING_SCORE) {
                gameOver = true;
                winner = isMultiplayerMode ? "Player 2" : "AI";
            }
        }
    }
    
    /**
     * Starts a new game
     */
    private void startGame() {
        // Reset ball
        ball.reset();
        
        // Reset paddles to center positions
        playerPaddle.reset();
        aiPaddle.reset();
        
        // Reset game state
        playerScore = 0;
        aiScore = 0;
        gameRunning = true;
        gameOver = false;
        winner = "";
        gamePaused = false;
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
        
        // Player 2 controls in multiplayer mode
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
                // Toggle pause state
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
        
        // Player 2 key releases in multiplayer mode
        if (isMultiplayerMode) {
            if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) {
                aiPaddle.setYVelocity(0);
            }
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
}