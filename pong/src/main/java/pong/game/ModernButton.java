package pong.game;

import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.LinearGradientPaint;
import java.awt.GradientPaint;
import java.awt.Point;

/**
 * A modern-looking button with hover effects for the Pong game.
 */
public class ModernButton extends JButton {
    private boolean hover;
    private boolean isCloseButton;
    
    /**
     * Creates a new modern button
     * @param text Text to display on button
     */
    public ModernButton(String text) {
        this(text, false);
    }
    
    /**
     * Creates a new modern button
     * @param text Text to display on button
     * @param isCloseButton Whether this is a close button (special styling)
     */
    public ModernButton(String text, boolean isCloseButton) {
        super(text);
        this.isCloseButton = isCloseButton;
        
        // Remove default button styling
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        
        // Set font
        setFont(new Font("Arial", Font.BOLD, isCloseButton ? 16 : 14));
        setForeground(Color.WHITE);
        
        // Add hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Choose colors based on type and state
        Color startColor, endColor, borderColor;
        
        if (isCloseButton) {
            if (hover || getModel().isPressed()) {
                startColor = new Color(220, 50, 50);
                endColor = new Color(180, 30, 30);
                borderColor = new Color(240, 80, 80);
            } else {
                startColor = new Color(180, 30, 30);
                endColor = new Color(140, 20, 20);
                borderColor = new Color(200, 50, 50);
            }
        } else {
            if (hover) {
                startColor = new Color(60, 170, 230);
                endColor = new Color(30, 120, 190);
                borderColor = new Color(100, 200, 255);
            } else if (getModel().isPressed()) {
                startColor = new Color(30, 120, 190);
                endColor = new Color(20, 80, 140);
                borderColor = new Color(70, 150, 200);
            } else {
                startColor = new Color(40, 130, 200);
                endColor = new Color(20, 100, 170);
                borderColor = new Color(70, 150, 200);
            }
        }
        
        // Create gradient background
        int radius = isCloseButton ? 8 : 12;
        RoundRectangle2D roundedRect = new RoundRectangle2D.Float(0, 0, width - 1, height - 1, radius, radius);
        
        LinearGradientPaint gradient = new LinearGradientPaint(
            new Point(0, 0),
            new Point(0, height),
            new float[]{0.0f, 1.0f},
            new Color[]{startColor, endColor}
        );
        
        g2d.setPaint(gradient);
        g2d.fill(roundedRect);
        
        // Draw border
        g2d.setColor(borderColor);
        g2d.draw(roundedRect);
        
        // Add highlight effect on top
        GradientPaint highlightGradient = new GradientPaint(
            0, 0, new Color(255, 255, 255, 100),
            0, height / 2, new Color(255, 255, 255, 0)
        );
        g2d.setPaint(highlightGradient);
        g2d.fill(new RoundRectangle2D.Float(1, 1, width - 3, height / 3, radius, radius));
        
        g2d.dispose();
        
        super.paintComponent(g);
    }
}