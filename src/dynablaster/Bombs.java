package dynablaster;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class Bombs {
    private final ArrayList<Bomb> bombs = new ArrayList<>();
    private final Object bombsLock = new Object();
    
    private final Image bombImage;

    public Bombs() {
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        bombImage = toolkit.getImage("bomb.png");
    }
    
    public void placeBomb(Player player, int x, int y) {
        if (!player.canPlaceBombs()) {
            return;
        }
        
        synchronized (bombsLock) {
            for (Bomb bomb : bombs) {
                if (bomb.x == x && bomb.y == y) {
                    return;
                }
            }
            
            bombs.add(new Bomb(player, x, y));
        }
    }
    
    public void update() {
        synchronized (bombsLock) {
            for (int i = bombs.size() - 1; i >= 0; i--) {
                Bomb bomb = bombs.get(i);
                if (bomb.shouldExplode()) {
                    bomb.exploded();
                    bombs.remove(i);
                }
            }
        }
    }
    
    public void draw(Graphics2D g, ImageObserver observer) {
        synchronized (bombsLock) {
            for (Bomb bomb : bombs) {
                g.drawImage(bombImage, bomb.x * Grid.TILE_SIZE, bomb.y * Grid.TILE_SIZE, observer);
            }
        }
    }
}
