package pong.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Representa una paleta en el juego de pong
 */
public class Paddle extends Rectangle {
    private int yVelocity = 0;
    private final int startX;
    private final int startY;
    private Color color = Color.WHITE;
    
    /**
     * Crea una nueva paleta en la posición especificada
     * @param x posición x inicial
     * @param y posición y inicial
     * @param width anchura de la paleta
     * @param height altura de la paleta
     * @param color color de la paleta
     */
    public Paddle(int x, int y, int width, int height, Color color) {
        super(x, y, width, height);
        this.startX = x;
        this.startY = y;
        this.color = color;
    }
    
    /**
     * Establece el color de la paleta
     * @param color el nuevo color
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /**
     * Establece la velocidad vertical de la paleta
     * @param yVelocity la velocidad vertical (-ve = arriba, +ve = abajo)
     */
    public void setYVelocity(int yVelocity) {
        this.yVelocity = yVelocity;
    }
    
    /**
     * Reinicia la paleta a su posición inicial
     */
    public void reset() {
        x = startX;
        y = startY;
        yVelocity = 0;
    }
    
    /**
     * Actualiza la posición de la paleta
     */
    public void update() {
        // Actualiza la posición basada en la velocidad
        y += yVelocity;
        
        // Mantiene la paleta dentro de los límites de la pantalla
        if (y < 0) {
            y = 0;
        }
        if (y > PongGame.HEIGHT - height) {
            y = PongGame.HEIGHT - height;
        }
    }
    
    /**
     * Dibuja la paleta en pantalla
     * @param g contexto gráfico donde dibujar
     */
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }
}