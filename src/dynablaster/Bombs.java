package dynablaster;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

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

    public void update(Grid grid) {
        synchronized (bombsLock) {
            List<Bomb> clone = (List<Bomb>) bombs.clone();
            for (Bomb bomb : clone) {
                if (bomb.shouldExplode()) {
                    explodeBomb(bomb, grid);
                }
            }
        }
    }

    private void explodeBomb(Bomb bomb, Grid grid) {
        if (bomb.hasExploded()) return;
        
        bomb.exploded();
        bombs.remove(bomb);

        destroyAt(bomb, grid, bomb.x, bomb.y - 1);
        destroyAt(bomb, grid, bomb.x, bomb.y + 1);
        destroyAt(bomb, grid, bomb.x - 1, bomb.y);
        destroyAt(bomb, grid, bomb.x + 1, bomb.y);
    }

    private void destroyAt(Bomb bomb, Grid grid, int x, int y) {
        grid.destroyTile(x, y);

        List<Bomb> clone = (List<Bomb>) bombs.clone();
        for (Bomb otherBomb : clone) {
            if (otherBomb != bomb
                    && otherBomb.x == x
                    && otherBomb.y == y) {
                explodeBomb(otherBomb, grid);
            }
        }
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        synchronized (bombsLock) {
            for (Bomb bomb : bombs) {
                g.drawImage(bombImage,
                        bomb.x * Grid.TILE_SIZE,
                        bomb.y * Grid.TILE_SIZE,
                        observer);
            }
        }
    }
}
