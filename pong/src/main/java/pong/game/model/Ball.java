package pong.game.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import pong.game.view.PongGame;

/**
 * Representa la pelota en el juego de pong
 */
public class Ball extends Rectangle {
    // Velocidad predeterminada aumentada de 2.5f a 4.0f
    private float xVelocity = 4.0f;  
    private float yVelocity = 4.0f;
    private float speedMultiplier = 1.0f;
    // Velocidad máxima aumentada a 15 para permitir un juego más rápido
    private final float MAX_SPEED = 15.0f;
    // Velocidad predeterminada aumentada de 2.5f a 4.0f
    private final float DEFAULT_SPEED = 4.0f;
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
     * Obtiene el multiplicador de velocidad actual
     * @return multiplicador de velocidad de la pelota
     */
    public float getSpeedMultiplier() {
        return speedMultiplier;
    }
    
    
    /**
     * Establece el color de la pelota
     * @param color el nuevo color
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /**
     * Establece el multiplicador de velocidad de la pelota
     * @param multiplier el multiplicador de velocidad
     */
    public void setSpeedMultiplier(float multiplier) {
        this.speedMultiplier = multiplier;
    }
    
    /**
     * Reinicia la pelota a su posición inicial y aleatoriza la dirección
     */
    public void reset() {
        x = 800 / 2 - width / 2;
        y = 600 / 2 - height / 2;
        
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
        if (y >= 600 - height) { // 600 es PongGame.HEIGHT
            y = 600 - height;
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
        float bounceAngle = normalizedRelativeIntersectionY * 0.75f; // 75% del ángulo máximo
        
        // Ajusta la velocidad en y basado en dónde golpeó la pelota en la paleta
        yVelocity = DEFAULT_SPEED * -bounceAngle;
        
        // Aumenta ligeramente la velocidad en cada golpe, hasta la velocidad máxima
        // Aumentado del 5% al 8% para una aceleración más rápida
        if (Math.abs(xVelocity) < MAX_SPEED) {
            xVelocity *= 1.08f; // 8% de incremento de velocidad (antes era 5%)
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
     * Obtiene la velocidad actual en x
     * @return la velocidad horizontal de la pelota
     */
    public float getXVelocity() {
        return xVelocity;
    }
    
    /**
     * Obtiene la velocidad actual en y
     * @return la velocidad vertical de la pelota
     */
    public float getYVelocity() {
        return yVelocity;
    }

    /**
     * Establece la posición de la pelota
     * @param x nueva posición x
     * @param y nueva posición y
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Dibuja la pelota
     * @param g contexto gráfico donde dibujar
     */
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval(x, y, width, height);
    }
}