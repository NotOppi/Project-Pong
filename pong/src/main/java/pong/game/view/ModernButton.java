package pong.game.view;

import javax.swing.JButton;

import pong.game.model.Theme;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Un botón con estilo personalizado para la interfaz del juego hola martin
 */
public class ModernButton extends JButton {
    private boolean isRound = false;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private Color buttonColor = new Color(70, 130, 180); // Azul Acero por defecto
    private Color textColor = Color.WHITE;
    
    /**
     * Crea un nuevo botón con estilo moderno
     * @param text el texto del botón
     */
    public ModernButton(String text) {
        super(text);
        setupButton();
    }
    
    /**
     * Crea un nuevo botón con estilo moderno
     * @param text el texto del botón
     * @param round si el botón debe ser redondo
     */
    public ModernButton(String text, boolean round) {
        super(text);
        this.isRound = round;
        setupButton();
    }
    
    /**
     * Configura la apariencia e interacción del botón
     */
    private void setupButton() {
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFont(new Font("Arial", Font.BOLD, 18));
        setForeground(textColor);
        
        // Eventos de ratón para manejar efectos de hover y presionado
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }
    
    /**
     * Actualiza los colores del botón basados en el tema actual
     * @param theme el tema a aplicar
     */
    public void applyTheme(Theme theme) {
        buttonColor = theme.getButtonColor();
        textColor = theme.getButtonTextColor();
        setForeground(textColor);
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Color base con efectos de hover y presionado
        Color baseColor = buttonColor;
        if (isPressed) {
            baseColor = baseColor.darker();
        } else if (isHovered) {
            baseColor = baseColor.brighter();
        }
        
        // Dibuja el fondo del botón
        g2d.setColor(baseColor);
        if (isRound) {
            g2d.fillOval(0, 0, width, height);
        } else {
            g2d.fillRoundRect(0, 0, width, height, 15, 15);
        }
        
        // Dibuja el borde
        g2d.setColor(baseColor.darker());
        if (isRound) {
            g2d.drawOval(0, 0, width - 1, height - 1);
        } else {
            g2d.drawRoundRect(0, 0, width - 1, height - 1, 15, 15);
        }
        
        g2d.dispose();
        
        // Dibuja el texto
        super.paintComponent(g);
    }
}