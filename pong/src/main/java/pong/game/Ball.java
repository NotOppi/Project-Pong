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
    
    // Maximum speed limits
    private final int MAX_X_SPEED = 7;
    private final int MAX_Y_SPEED = 5;
    
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
 * Handles ball deflection based on where it hit the paddle
 * @param paddle The paddle that the ball collided with
 */
public void deflectFromPaddle(Paddle paddle) {
    // Reverse X direction (basic bounce behavior)
    xVelocity = -xVelocity;
    
    // Increase speed but respect the maximum limits
    if (xVelocity > 0) {
        xVelocity = Math.min(xVelocity + 1, MAX_X_SPEED);
    } else {
        xVelocity = Math.max(xVelocity - 1, -MAX_X_SPEED);
    }
    
    // Calculate relative hit position (-0.5 to 0.5, where 0 is center)
    int paddleCenter = paddle.getY() + paddle.getHeight() / 2;
    int ballCenter = this.y + this.height / 2;
    float relativeIntersectY = (paddleCenter - ballCenter) / (float)(paddle.getHeight() / 2);
    
    // Clamp the value between -0.5 and 0.5
    relativeIntersectY = Math.max(-0.5f, Math.min(0.5f, relativeIntersectY));
    
    // Calculate influence factor - how much the hit position affects direction
    float hitInfluence = Math.abs(relativeIntersectY) * 1.5f + 0.25f; // Range: 0.25-1.0
    hitInfluence = Math.min(hitInfluence, 1.0f);
    
    // Get the previous direction but limit its impact for center hits
    int previousDirection = (yVelocity != 0) ? (yVelocity / Math.abs(yVelocity)) : 0;
    int baseVelocity = previousDirection * Math.min(Math.abs(yVelocity), MAX_Y_SPEED/2);
    
    // Calculate new y velocity based on hit position
    int hitPositionVelocity = (int)(-relativeIntersectY * MAX_Y_SPEED * 1.5);
    
    // Blend previous direction with hit position
    yVelocity = (int)(hitPositionVelocity * hitInfluence + baseVelocity * (1-hitInfluence));
    
    // Add small random component to prevent straight horizontal movement
    // The closer to center, the more randomness we add
    float centerProximity = 1.0f - Math.abs(relativeIntersectY * 2); // 1.0 at center, 0 at edges
    if (centerProximity > 0.7f) { // Only add randomness for near-center hits
        int randomComponent = (Math.random() > 0.5) ? 1 : -1; // Random direction
        int randomMagnitude = 1 + (int)(Math.random() * 2); // Random value 1-2
        yVelocity += randomComponent * randomMagnitude;
    }
    
    // Ensure the Y velocity stays within limits
    yVelocity = Math.max(-MAX_Y_SPEED, Math.min(MAX_Y_SPEED, yVelocity));
    
    // Ensure the ball always has some minimal vertical movement
    if (Math.abs(yVelocity) < 1) {
        yVelocity = (Math.random() > 0.5) ? 1 : -1;
    }
    
    // Ensure horizontal movement is always significant
    if (Math.abs(yVelocity) > Math.abs(xVelocity)) {
        yVelocity = (yVelocity > 0) ? 
            Math.min(yVelocity, MAX_Y_SPEED) : 
            Math.max(yVelocity, -MAX_Y_SPEED);
    }
}
    
    /**
     * Reverses the horizontal direction and slightly increases speed
     * while respecting maximum speed limits
     * @deprecated Use deflectFromPaddle for more realistic physics
     */
    public void reverseXDirection() {
        xVelocity = -xVelocity;
        
        // Increase speed but respect the maximum limits
        if (xVelocity > 0) {
            xVelocity = Math.min(xVelocity + 1, MAX_X_SPEED);
        } else {
            xVelocity = Math.max(xVelocity - 1, -MAX_X_SPEED);
        }
        
        // Add a bit of randomness to y velocity but respect limits
        if (Math.random() > 0.5) {
            yVelocity = Math.min(yVelocity + 1, MAX_Y_SPEED);
        } else {
            yVelocity = Math.max(yVelocity - 1, -MAX_Y_SPEED);
        }
    }
    
    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}