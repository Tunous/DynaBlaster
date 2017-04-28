package dynablaster;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MainPanel extends JPanel implements ActionListener {

    private final Timer moveTimer;
    private final Player player = new Player(0, 0);
    private final Grid grid = new Grid();
    private final Bombs bombs = new Bombs();

    public MainPanel() {
        initComponents();

        KeyListenerWrapper listener = KeyListenerWrapper.init(new KeyAdapter() {
            private int latestKeyPressedCode;

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        player.setMovementDirection(Direction.UP);
                        latestKeyPressedCode = e.getKeyCode();
                        break;
                    case KeyEvent.VK_DOWN:
                        player.setMovementDirection(Direction.DOWN);
                        latestKeyPressedCode = e.getKeyCode();
                        break;
                    case KeyEvent.VK_LEFT:
                        player.setMovementDirection(Direction.LEFT);
                        latestKeyPressedCode = e.getKeyCode();
                        break;
                    case KeyEvent.VK_RIGHT:
                        player.setMovementDirection(Direction.RIGHT);
                        latestKeyPressedCode = e.getKeyCode();
                        break;
                    case KeyEvent.VK_SPACE:
                        int x = (player.getX() + 8) / 16 + 1;
                        int y = (player.getY() + 8) / 16 + 1;

                        bombs.placeBomb(player, x, y);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_RIGHT:
                        if (latestKeyPressedCode == e.getKeyCode()) {
                            player.setMovementDirection(Direction.NONE);
                        }
                        break;
                    default:
                        break;
                }
            }

        }, false);

        addKeyListener(listener);

        moveTimer = new Timer(17, this);
        moveTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent ActionEvent) {
        player.move(grid);
        bombs.update();

        repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setPreferredSize(new java.awt.Dimension(208, 208));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 208, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 208, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        grid.draw(g2, this);
        player.draw(g2, this);
        bombs.draw(g2, this);

        Toolkit.getDefaultToolkit().sync();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
