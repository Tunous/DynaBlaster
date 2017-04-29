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

    public final GameController gameController = new GameController();
    private final Timer gameTimer = new Timer(17, new TickListener());

    public MainPanel() {
        initComponents();
    }

    private void initComponents() {
        final int width = Grid.WIDTH * Grid.TILE_SIZE;
        final int height = Grid.HEIGHT * Grid.TILE_SIZE;
        final Dimension size = new Dimension(width, height);
        setPreferredSize(size);

        gameController.registerKeyListener(this);
        gameTimer.start();
        
        addMouseListener(new MouseListener());
    }
    
    public void setFrame(MainFrame frame) {
        gameController.setFrame(frame);
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
