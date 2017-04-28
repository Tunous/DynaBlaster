package dynablaster;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

public class Bombs {

    private final ArrayList<Bomb> bombs = new ArrayList<>();

    private final Image bombImage;

    public Bombs() {
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        bombImage = toolkit.getImage("bomb.png");
    }

    public void placeBomb(Player player, int x, int y) {
        if (!player.canPlaceBombs()) {
            return;
        }

        synchronized (bombs) {
            for (Bomb bomb : bombs) {
                if (bomb.x == x && bomb.y == y) {
                    return;
                }
            }

            bombs.add(player.placeBomb(x, y));
        }
    }

    public void update(Grid grid, Player player) {
        synchronized (bombs) {
            List<Bomb> clone = (List<Bomb>) bombs.clone();
            for (Bomb bomb : clone) {
                if (bomb.shouldExplode()) {
                    explodeBomb(bomb, grid, player);
                }
            }
        }
    }

    private void explodeBomb(Bomb bomb, Grid grid, Player player) {
        if (bomb.hasExploded()) {
            return;
        }

        bomb.exploded();
        bombs.remove(bomb);

        for (int i = 0; i < bomb.range; i++) {
            if (destroyAt(bomb, grid, player, bomb.x, bomb.y - i)) {
                break;
            }
        }
        for (int i = 0; i < bomb.range; i++) {
            if (destroyAt(bomb, grid, player, bomb.x, bomb.y + i)) {
                break;
            }
        }
        for (int i = 0; i < bomb.range; i++) {
            if (destroyAt(bomb, grid, player, bomb.x - i, bomb.y)) {
                break;
            }

        }
        for (int i = 0; i < bomb.range; i++) {
            if (destroyAt(bomb, grid, player, bomb.x + i, bomb.y)) {
                break;
            }
        }
    }

    private boolean destroyAt(Bomb bomb, Grid grid, Player player, int x, int y) {
        if (grid.destroyTile(x, y)) {
            return true;
        }
        
        if (player.getTileX() == x && player.getTileY() == y) {
            player.kill();
        }

        List<Bomb> clone = (List<Bomb>) bombs.clone();
        for (Bomb otherBomb : clone) {
            if (otherBomb != bomb
                    && otherBomb.x == x
                    && otherBomb.y == y) {
                explodeBomb(otherBomb, grid, player);
                return true;
            }
        }

        return false;
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        synchronized (bombs) {
            for (Bomb bomb : bombs) {
                g.drawImage(bombImage,
                        bomb.x * Grid.TILE_SIZE,
                        bomb.y * Grid.TILE_SIZE,
                        observer);
            }
        }
    }
}
