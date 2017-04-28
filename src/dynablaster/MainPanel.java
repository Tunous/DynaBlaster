package dynablaster;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MainPanel extends JPanel implements Player.GridChecker {

    private final Image gracz;
    private final Image indestructible;
    private final Image bomb;
    private final Image grass;
    private final Image grassShadow;
    private final Timer moveTimer;

    private Player player = new Player(0, 0);

    private final int grid[][] = new int[13][13];

    public MainPanel() {
        initComponents();

        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        gracz = toolkit.getImage("gracz.png");
        indestructible = toolkit.getImage("indestructible.png");
        bomb = toolkit.getImage("bomb.png");
        grass = toolkit.getImage("grass.png");
        grassShadow = toolkit.getImage("grass-shadow.png");

        for (int x = 0; x < 13; x++) {
            for (int y = 0; y < 13; y++) {
                if (x == 0 || y == 0 || x == 12 || y == 12 || x % 2 == 0 && y % 2 == 0) {
                    grid[x][y] = 1;
                } else {
                    grid[x][y] = 0;
                }
            }
        }
        
        grid[3][2] = 1;
        grid[3][3] = 1;

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
                        int xTile = (player.getX() + 8) / 16 + 1;
                        int yTile = (player.getY() + 8) / 16 + 1;

                        grid[xTile][yTile] = 2;
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

        moveTimer = new Timer(17, (ActionEvent) -> {
            player.move(this);

            repaint();
        });
        moveTimer.start();
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

        for (int x = 0; x < 13; x++) {
            for (int y = 0; y < 13; y++) {
                Image tileImage;
                if (grid[x][y] == 1) {
                    tileImage = indestructible;
                } else if (y > 0 && grid[x][y - 1] == 1) {
                    tileImage = grassShadow;
                } else {
                    tileImage = grass;
                }
                g2.drawImage(tileImage, x * 16, y * 16, this);

                Image image = null;
                switch (grid[x][y]) {
                    case 2:
                        image = bomb;
                        break;
                    default:
                        break;
                }
                if (image != null) {
                    g2.drawImage(image, x * 16, y * 16, this);
                }
            }
        }

        g2.drawImage(gracz, player.getX() + 13, player.getY() + 7, this);

        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public boolean canMoveTo(int tileX, int tileY) {
        if (tileX < 0 || tileY < 0 || tileX > 12 || tileY > 12) return false;
        return grid[tileX][tileY] != 1;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
