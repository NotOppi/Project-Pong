package pong.game.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import pong.game.view.PongGame;

/**
 * Representa la pelota en el juego de pong
 */
public class Ball extends Rectangle {
    // Constantes
    private static final float MAX_SPEED = 15.0f;
    private static final float DEFAULT_SPEED = 4.0f;
    private static final float BOUNCE_ANGLE_FACTOR = 0.75f;
    private static final float SPEED_INCREASE_FACTOR = 1.08f;
    
    // Variables de estado
    private float xVelocity = DEFAULT_SPEED;  
    private float yVelocity = DEFAULT_SPEED;
    private float speedMultiplier = 1.0f;
    private Color color = Color.WHITE;
    
    /**
     * Crea una nueva pelota en la posición especificada
     * @param x posición x inicial
     * @param y posición y inicial
     * @param size tamaño de la pelota (ancho y alto)
     */
    public Ball(int x, int y, int size) {
        super(x, y, size, size);
        reset();
    }

    /**
     * Reinicia la pelota a su posición inicial y aleatoriza la dirección
     */
    public void reset() {
        x = PongGame.WIDTH / 2 - width / 2;
        y = PongGame.HEIGHT / 2 - height / 2;
        
        // Aleatoriza la dirección inicial
        xVelocity = (Math.random() > 0.5 ? 1 : -1) * DEFAULT_SPEED;
        yVelocity = (Math.random() > 0.5 ? 1 : -1) * DEFAULT_SPEED;
    }
    
    /**
     * Actualiza la posición de la pelota
     */
    public void update() {
        // Aplica el multiplicador de velocidad
        float actualXVelocity = xVelocity * speedMultiplier;
        float actualYVelocity = yVelocity * speedMultiplier;
        
        // Actualiza la posición
        x += (int)actualXVelocity;
        y += (int)actualYVelocity;
        
        // Rebota en las paredes superior e inferior
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
     * Hace rebotar la pelota en una paleta según donde golpeó
     * @param paddle la paleta que fue golpeada
     */
    public void deflectFromPaddle(Paddle paddle) {
        // Invierte la dirección en x
        xVelocity = -xVelocity;
        
        // Ajusta el ángulo basado en dónde golpeó la pelota en la paleta
        float relativeIntersectY = (paddle.y + (paddle.height / 2)) - (y + (height / 2));
        float normalizedRelativeIntersectionY = relativeIntersectY / (paddle.height / 2);
        float bounceAngle = normalizedRelativeIntersectionY * BOUNCE_ANGLE_FACTOR;
        
        // Ajusta la velocidad en y basado en dónde golpeó la pelota en la paleta
        yVelocity = DEFAULT_SPEED * -bounceAngle;
        
        // Aumenta ligeramente la velocidad en cada golpe, hasta la velocidad máxima
        if (Math.abs(xVelocity) < MAX_SPEED) {
            xVelocity *= SPEED_INCREASE_FACTOR;
        }
        
        // Evita que la pelota se quede atrapada en la paleta
        if (paddle.x < PongGame.WIDTH / 2) {
            // Paleta izquierda
            x = paddle.x + paddle.width;
        } else {
            // Paleta derecha
            x = paddle.x - width;
        }
    }
    
    /**
     * Dibuja la pelota
     * @param g contexto gráfico donde dibujar
     */
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x, y, width, height);
    }

    // Getters y setters
    
    public float getSpeedMultiplier() {
        return speedMultiplier;
    }
    
    public void setSpeedMultiplier(float multiplier) {
        this.speedMultiplier = multiplier;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public float getXVelocity() {
        return xVelocity;
    }
    
    public void setXVelocity(float xVelocity) {
        this.xVelocity = xVelocity;
    }
    
    public float getYVelocity() {
        return yVelocity;
    }
    
    public void setYVelocity(float yVelocity) {
        this.yVelocity = yVelocity;
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
