package pong.game.view;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Un botón con estilo personalizado para la interfaz del juego
 */
public class ModernButton extends JButton {
    /** Indica si el botón es redondo */
    private boolean isRound = false;
    /** Indica si el cursor está sobre el botón */
    private boolean isHovered = false;
    /** Indica si el botón está presionado */
    private boolean isPressed = false;
    /** Color de fondo del botón */
    private Color buttonColor = new Color(70, 130, 180); // Azul Acero por defecto
    /** Color del texto del botón */
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
     * Establece el color del botón
     * @param color el nuevo color para el botón
     */
    public void setButtonColor(Color color) {
        this.buttonColor = color;
        repaint();
    }
    
    /**
     * Establece el color del texto
     * @param color el nuevo color para el texto
     */
    public void setTextColor(Color color) {
        this.textColor = color;
        setForeground(color);
        repaint();
    }
    
    @Override
    public void setText(String text) {
        super.setText(text);
        // Forzar el repintado para asegurar que se muestre el nuevo texto
        if (isShowing()) {
            revalidate();
            repaint();
        }
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
        
        // Para botones redondos, dibuja el texto manualmente en lugar de usar la implementación predeterminada
        if (isRound) {
            String text = getText();
            if (text != null && !text.isEmpty()) {
                g2d.setColor(textColor);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                g2d.drawString(text, (width - textWidth) / 2, (height - textHeight) / 2 + fm.getAscent());
            }
        } else {
            g2d.dispose();
            super.paintComponent(g);
            return;
        }
        
        g2d.dispose();
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
                if (!isEnabled()) return;
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!isEnabled()) return;
                isHovered = false;
                repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (!isEnabled()) return;
                if (e.getButton() == MouseEvent.BUTTON1) {
                    isPressed = true;
                    repaint();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!isEnabled()) return;
                if (e.getButton() == MouseEvent.BUTTON1) {
                    isPressed = false;
                    repaint();
                }
            }
        });
    }
}