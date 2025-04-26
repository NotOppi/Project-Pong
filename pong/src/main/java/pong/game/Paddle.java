package pong.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Represents a paddle in the pong game
 */
public class Paddle extends Rectangle {
    private int yVelocity = 0;
    private final int startX;
    private final int startY;
    private Color color = Color.WHITE;
    
    /**
     * Creates a new paddle at the specified position
     */
    public Paddle(int x, int y, int width, int height, Color color) {
        super(x, y, width, height);
        this.startX = x;
        this.startY = y;
        this.color = color;
    }
    
    /**
     * Sets the paddle color
     * @param color the new color
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /**
     * Sets the paddle's vertical movement speed
     * @param yVelocity the vertical velocity (-ve = up, +ve = down)
     */
    public void setYVelocity(int yVelocity) {
        this.yVelocity = yVelocity;
    }
    
    /**
     * Resets the paddle to starting position
     */
    public void reset() {
        x = startX;
        y = startY;
        yVelocity = 0;
    }
    
    /**
     * Updates the paddle's position
     */
    public void update() {
        y += yVelocity;
        
        // Keep paddle within screen bounds
        if (y < 0) {
            y = 0;
        }
        if (y > PongGame.HEIGHT - height) {
            y = PongGame.HEIGHT - height;
        }
    }
    
    /**
     * Draws the paddle
     */
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }
}