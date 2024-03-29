package dynablaster;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MainPanel extends JPanel {

    private GameController gameController;
    private final Timer gameTimer = new Timer(17, new TickListener());
    
    public void setGameController(GameController controller) {
        gameController = controller;
        
        gameController.registerKeyListener(this);
        gameTimer.start();

        addMouseListener(new MouseListener());
    }

    @Override
    public Dimension getPreferredSize() {
        return Grid.SIZE;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        gameController.draw(g2, this);
        Toolkit.getDefaultToolkit().sync();
    }

    private class MouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            requestFocusInWindow();
        }
    }

    private class TickListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            gameController.update();
            repaint();
        }
    }
}
