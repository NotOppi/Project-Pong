package pong.game.view.screens;

import pong.game.model.GameModel;
import pong.game.view.ModernButton;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Font;

/**
 * Pantalla de instrucciones del juego
 */
public class InstructionsScreen extends JPanel {
    private GameModel model;
    private ModernButton closeButton;
    
    public InstructionsScreen(GameModel model) {
        this.model = model;
        setLayout(null);
        setOpaque(false);
        
        // Configurar botón de cerrar
        closeButton = new ModernButton("X", true);
        closeButton.setBounds(720, 20, 40, 40);
        closeButton.setFocusable(false);
        closeButton.addActionListener(e -> {
            model.setCurrentScreen("MAIN_MENU");
        });
        add(closeButton);
        
        // Aplicar tema
        if (model.getCurrentTheme() != null) {
            closeButton.applyTheme(model.getCurrentTheme());
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Solo dibuja si hay un tema configurado
        if (model.getCurrentTheme() == null) return;
        
        // Dibuja fondo semitransparente
        g.setColor(model.getCurrentTheme().getPanelOverlayColor());
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Dibuja título de instrucciones
        g.setColor(model.getCurrentTheme().getTextColor());
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Cómo Jugar", getWidth() / 2 - 120, 80);
        
        // Dibuja texto de instrucciones
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String[] instructions;
        
        if (model.isMultiplayerMode()) {
            instructions = new String[] {
                "• Jugador 1: Usa la tecla W para mover la paleta hacia arriba",
                "• Jugador 1: Usa la tecla S para mover la paleta hacia abajo",
                "• Jugador 2: Usa la FLECHA ARRIBA para mover la paleta hacia arriba", 
                "• Jugador 2: Usa la FLECHA ABAJO para mover la paleta hacia abajo",
                "• Anota puntos haciendo que la pelota pase la paleta del oponente",
                "• Presiona ESPACIO para pausar/reanudar el juego",
                "• El primer jugador en llegar a 10 puntos gana!"
            };
        } else {
            instructions = new String[] {
                "• Usa la tecla W para mover la paleta hacia arriba",
                "• Usa la tecla S para mover la paleta hacia abajo",
                "• Anota puntos haciendo que la pelota pase la paleta de la IA",
                "• Presiona ESPACIO para pausar/reanudar el juego",
                "• El primer jugador en llegar a 10 puntos gana!"
            };
        }
        
        int yPos = 150;
        for (String line : instructions) {
            g.drawString(line, getWidth() / 2 - 200, yPos);
            yPos += 40;
        }
    }
}
