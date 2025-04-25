package pong.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Represents a paddle in the game that can be controlled by a player or AI.
 */
public class Paddle {
    private int x, y;
    private int width, height;
    private int yVelocity;
    private Color color;
    
    /**
     * Creates a new paddle
     */
    public Paddle(int x, int y, int width, int height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }
    
    /**
     * Updates the paddle position
     */
    public void update() {
        y += yVelocity;
        
        // Keep paddle within bounds
        if (y < 0) {
            y = 0;
        }
        if (y + height > PongGame.HEIGHT) {
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
     * Sets the vertical velocity of the paddle
     */
    public void setYVelocity(int yVelocity) {
        this.yVelocity = yVelocity;
    }
    
    /**
     * Checks if this paddle intersects with a ball
     */
    public boolean intersects(Ball ball) {
        return new Rectangle(x, y, width, height)
            .intersects(new Rectangle(ball.getX(), ball.getY(), ball.getWidth(), ball.getHeight()));
    }
    
    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    
    // Setters
    public void setY(int y) { this.y = y; }
}