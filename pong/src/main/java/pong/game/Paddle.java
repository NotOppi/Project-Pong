package pong.game;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Represents a paddle in the game that can be controlled by a player or AI.
 */
public class Paddle {
    private int x, y;
    private int width, height;
    private int yVelocity;
    private Color color;
    private int initialY; // Store initial Y position for reset
    
    /**
     * Creates a new paddle
     */
    public Paddle(int x, int y, int width, int height, Color color) {
        this.x = x;
        this.y = y;
        this.initialY = y; // Store initial position
        this.width = width;
        this.height = height;
        this.yVelocity = 0;
        this.color = color;
    }
    
    /**
     * Resets the paddle to its center starting position
     */
    public void reset() {
        this.y = initialY;
        this.yVelocity = 0;
    }
    
    /**
     * Updates the paddle position
     */
    public void update() {
        y += yVelocity;
        
        // Keep paddle within screen bounds
        if (y <= 0) {
            y = 0;
        } else if (y + height >= PongGame.HEIGHT) {
            y = PongGame.HEIGHT - height;
        }
    }
    
    /**
     * Draws the paddle on screen
     */
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }
    
    /**
     * Checks if this paddle intersects with the given ball
     * @param ball The ball to check collision with
     * @return true if collision detected, false otherwise
     */
    public boolean intersects(Ball ball) {
        // Create rectangle representations for collision detection
        return ball.getX() < x + width &&
               ball.getX() + ball.getWidth() > x &&
               ball.getY() < y + height &&
               ball.getY() + ball.getHeight() > y;
    }
    
    /**
     * Sets the paddle's vertical velocity
     */
    public void setYVelocity(int yVelocity) {
        this.yVelocity = yVelocity;
    }
    
    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}