package pong.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Represents the ball in the pong game
 */
public class Ball extends Rectangle {
    // Increase default velocity from 2.5f to 4.0f
    private float xVelocity = 4.0f;  
    private float yVelocity = 4.0f;
    private float speedMultiplier = 1.0f;
    // Increase max speed to 15 to allow faster gameplay
    private final float MAX_SPEED = 15.0f;
    // Increase default speed from 2.5f to 4.0f
    private final float DEFAULT_SPEED = 4.0f;
    private Color color = Color.WHITE;
    
    /**
     * Creates a new ball at the specified position
     */
    public Ball(int x, int y, int size) {
        super(x, y, size, size);
        reset();
    }
    
    /**
     * Sets the ball color
     * @param color the new color
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /**
     * Sets the ball speed multiplier
     * @param multiplier the speed multiplier
     */
    public void setSpeedMultiplier(float multiplier) {
        this.speedMultiplier = multiplier;
    }
    
    /**
     * Resets the ball to starting position and randomizes direction
     */
    public void reset() {
        x = PongGame.WIDTH / 2 - width / 2;
        y = PongGame.HEIGHT / 2 - height / 2;
        
        // Randomize initial direction
        xVelocity = (Math.random() > 0.5 ? 1 : -1) * DEFAULT_SPEED;
        yVelocity = (Math.random() > 0.5 ? 1 : -1) * DEFAULT_SPEED;
    }
    
    /**
     * Updates the ball's position
     */
    public void update() {
        // Apply speed multiplier
        float actualXVelocity = xVelocity * speedMultiplier;
        float actualYVelocity = yVelocity * speedMultiplier;
        
        // Update position
        x += (int)actualXVelocity;
        y += (int)actualYVelocity;
        
        // Bounce off top and bottom walls
        if (y <= 0) {
            y = 0;
            yVelocity = Math.abs(yVelocity);
        }
        if (y >= PongGame.HEIGHT - height) {
            y = PongGame.HEIGHT - height;
            yVelocity = -Math.abs(yVelocity);
        }
    }
    
    /**
     * Deflects the ball from a paddle based on where it hit
     * @param paddle the paddle that was hit
     */
    public void deflectFromPaddle(Paddle paddle) {
        // Reverse x direction
        xVelocity = -xVelocity;
        
        // Adjust angle based on where the ball hit the paddle
        float relativeIntersectY = (paddle.y + (paddle.height / 2)) - (y + (height / 2));
        float normalizedRelativeIntersectionY = relativeIntersectY / (paddle.height / 2);
        float bounceAngle = normalizedRelativeIntersectionY * 0.75f; // 75% of max angle
        
        // Adjust y velocity based on where the ball hit the paddle
        yVelocity = DEFAULT_SPEED * -bounceAngle;
        
        // Increase speed slightly on each hit, up to max speed
        // Increased from 5% to 8% for faster acceleration
        if (Math.abs(xVelocity) < MAX_SPEED) {
            xVelocity *= 1.08f; // 8% speed increase (was 5%)
        }
        
        // Prevent ball from getting stuck in paddle
        if (paddle.x < PongGame.WIDTH / 2) {
            // Left paddle
            x = paddle.x + paddle.width;
        } else {
            // Right paddle
            x = paddle.x - width;
        }
    }
    
    /**
     * Get the current x velocity
     * @return the ball's horizontal velocity
     */
    public float getXVelocity() {
        return xVelocity;
    }
    
    /**
     * Get the current y velocity
     * @return the ball's vertical velocity
     */
    public float getYVelocity() {
        return yVelocity;
    }
    
    /**
     * Draws the ball
     */
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x, y, width, height);
    }
}