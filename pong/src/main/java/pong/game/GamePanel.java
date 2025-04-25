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
    private JButton closeInstructionsButton;
    private boolean showingInstructions = false;
    
    // Game state
    private int playerScore = 0;
    private int aiScore = 0;
    private boolean gameRunning = false;
    private boolean gamePaused = false;
    private boolean gameOver = false;
    private String winner = "";
    private final int WINNING_SCORE = 10;
    
    // Timer for game loop
    private Timer gameTimer;
    private final int DELAY = 10;
    private final int PADDLE_SPEED = 6;
    
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
                                  15, paddleHeight, Color.WHITE);
        aiPaddle = new Paddle(PongGame.WIDTH - 45, PongGame.HEIGHT / 2 - paddleHeight / 2, 
                             15, paddleHeight, Color.WHITE);
        ball = new Ball(PongGame.WIDTH / 2, PongGame.HEIGHT / 2, 15);
        
        // Set up game timer
        gameTimer = new Timer(DELAY, this);
        gameTimer.start(); // Start the timer right away for UI updates
    }
    
    /**
     * Initializes UI components
     */
    private void initializeUI() {
        // Start Game button
        startButton = new ModernButton("Start Game");
        startButton.setBounds(PongGame.WIDTH / 2 - 100, PongGame.HEIGHT / 2 + 30, 200, 45);
        startButton.setFocusable(false); // Don't steal keyboard focus
        startButton.addActionListener(e -> {
            startGame();
            this.requestFocus(); // Return focus to game panel for keyboard input
        });
        add(startButton);
        
        // How To Play button
        howToPlayButton = new ModernButton("How To Play");
        howToPlayButton.setBounds(PongGame.WIDTH / 2 - 100, PongGame.HEIGHT / 2 + 85, 200, 45);
        howToPlayButton.setFocusable(false);
        howToPlayButton.addActionListener(e -> {
            showingInstructions = true;
            this.requestFocus();
            repaint();
            closeInstructionsButton.setVisible(true);
        });
        add(howToPlayButton);
        
        // Close Instructions button (X button)
        closeInstructionsButton = new ModernButton("X", true);
        closeInstructionsButton.setBounds(PongGame.WIDTH - 60, 20, 40, 40);
        closeInstructionsButton.setFocusable(false);
        closeInstructionsButton.addActionListener(e -> {
            showingInstructions = false;
            this.requestFocus();
            repaint();
        });
        closeInstructionsButton.setVisible(false); // Initially hidden
        add(closeInstructionsButton);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
        
        // Show/hide buttons based on game state and instructions visibility
        boolean showMainButtons = (!gameRunning || gameOver) && !showingInstructions;
        startButton.setVisible(showMainButtons);
        howToPlayButton.setVisible(showMainButtons);
        closeInstructionsButton.setVisible(showingInstructions);
        
        // Draw instructions overlay if needed
            if (showingInstructions) {
                drawInstructions(g);
            }
    }
    
    /**
     * Draws all game elements on the screen
     */
    private void draw(Graphics g) {
        // Draw background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, PongGame.WIDTH, PongGame.HEIGHT);
        
        // Draw paddles
        playerPaddle.draw(g);
        aiPaddle.draw(g);
        
        // Draw ball (only if game is running and not over)
        if (gameRunning && !gameOver) {
            ball.draw(g);
        }
        
        // Draw center dividing line
        g.setColor(Color.WHITE);
        for (int i = 0; i < PongGame.HEIGHT; i += 50) {
            g.fillRect(PongGame.WIDTH / 2 - 1, i, 2, 25);
        }
        
        // Draw scores
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString(String.valueOf(playerScore), PongGame.WIDTH / 2 - 50, 50);
        g.drawString(String.valueOf(aiScore), PongGame.WIDTH / 2 + 30, 50);
        
        // Draw game over message
        if (gameOver) {
            g.setColor(new Color(255, 215, 0, 220)); // Semi-transparent gold
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("GAME OVER", PongGame.WIDTH / 2 - 150, PongGame.HEIGHT / 2 - 50);
            
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString(winner + " Wins!", PongGame.WIDTH / 2 - 70, PongGame.HEIGHT / 2);
            
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press SPACE to play again", PongGame.WIDTH / 2 - 110, PongGame.HEIGHT / 2 + 40);
        }
        // Draw pause message if game is paused and not over
        else if (gamePaused && gameRunning) {
            g.setColor(new Color(255, 255, 255, 200)); // Semi-transparent white
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("PAUSED", PongGame.WIDTH / 2 - 100, PongGame.HEIGHT / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press SPACE to continue", PongGame.WIDTH / 2 - 110, PongGame.HEIGHT / 2 + 40);
        }
    }

     /**
     * Draws the how-to-play instructions overlay
     */
    private void drawInstructions(Graphics g) {
        // Draw semi-transparent background
        g.setColor(new Color(0, 0, 0, 220)); // Almost black, semi-transparent
        g.fillRect(0, 0, PongGame.WIDTH, PongGame.HEIGHT);
        
        // Draw instructions title
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("How To Play", PongGame.WIDTH / 2 - 120, 80);
        
        // Draw instructions text
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String[] instructions = {
            "• Use W key to move paddle up",
            "• Use S key to move paddle down",
            "• Score points by getting the ball past the AI paddle",
            "• The ball will bounce at different angles depending",
            "  on where it hits your paddle",
            "• Press SPACE to pause/resume the game",
            "• First player to reach 10 points wins!"
        };
        
        int yPos = 150;
        for (String line : instructions) {
            g.drawString(line, PongGame.WIDTH / 2 - 200, yPos);
            yPos += 40;
        }
        
        // The Close (X) button is handled separately as a JButton
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
                // Simple AI for opponent paddle
                int aiPaddleCenterY = aiPaddle.getY() + aiPaddle.getHeight() / 2;
                int ballCenterY = ball.getY() + ball.getHeight() / 2;
                
                if (aiPaddleCenterY < ballCenterY) {
                    aiPaddle.setYVelocity(PADDLE_SPEED - 2); // Slightly slower than player
                } else if (aiPaddleCenterY > ballCenterY) {
                    aiPaddle.setYVelocity(-(PADDLE_SPEED - 2)); // Slightly slower than player
                } else {
                    aiPaddle.setYVelocity(0);
                }
                
                aiPaddle.update();
                
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
                winner = "Player";
            }
        }
        
        // AI scores (ball goes past player paddle)
        if (ball.getX() <= 0) {
            aiScore++;
            ball.reset();
            
            // Check for win condition
            if (aiScore >= WINNING_SCORE) {
                gameOver = true;
                winner = "AI";
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
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
}