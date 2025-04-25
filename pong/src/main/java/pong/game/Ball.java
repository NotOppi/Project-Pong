package pong.game;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Represents the ball in the game.
 */
public class Ball {
    private int x, y;
    private int width, height;
    private int xVelocity, yVelocity;
    
    /**
     * Creates a new ball
     */
    public Ball(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.width = size;
        this.height = size;
        resetVelocity();
    }
    
    /**
     * Updates the ball position
     */
    public void update() {
        x += xVelocity;
        y += yVelocity;
        
        // Handle top and bottom bounds
        if (y <= 0) {
            y = 0;
            yVelocity = -yVelocity;
        } else if (y + height >= PongGame.HEIGHT) {
            y = PongGame.HEIGHT - height;
            yVelocity = -yVelocity;
        }
    }
    
    /**
     * Resets the ball to the center with a random direction
     */
    public void reset() {
        x = PongGame.WIDTH / 2 - width / 2;
        y = PongGame.HEIGHT / 2 - height / 2;
        resetVelocity();
    }
    
    /**
     * Sets a random initial velocity
     */
    private void resetVelocity() {
        // Give ball an initial velocity in a random direction
        xVelocity = (Math.random() > 0.5) ? 3 : -3;
        yVelocity = (Math.random() > 0.5) ? 3 : -3;
    }
    
    /**
     * Draws the ball on screen
     */
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillOval(x, y, width, height);
    }
    
    /**
     * Reverses the horizontal direction and slightly increases speed
     */
    public void reverseXDirection() {
        xVelocity = -xVelocity;
        if (xVelocity > 0) {
            xVelocity++; // Speed up slightly
        } else {
            xVelocity--; // Speed up slightly
        }
        
        // Also add a bit of randomness to y velocity
        if (Math.random() > 0.5) {
            yVelocity++;
        } else {
            yVelocity--;
        }
    }
    
    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}