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

    public void update(Grid grid, Players players) {
        synchronized (bombs) {
            List<Bomb> clone = (List<Bomb>) bombs.clone();
            for (Bomb bomb : clone) {
                if (bomb.shouldExplode()) {
                    explodeBomb(bomb, grid, players);
                }
            }
        }
    }

    private void explodeBomb(Bomb bomb, Grid grid, Players players) {
        if (bomb.hasExploded()) {
            return;
        }

        bomb.exploded();
        bombs.remove(bomb);

        for (int i = 0; i < bomb.range; i++) {
            if (destroyAt(bomb, grid, players, bomb.x, bomb.y - i)) {
                break;
            }
        }
        for (int i = 0; i < bomb.range; i++) {
            if (destroyAt(bomb, grid, players, bomb.x, bomb.y + i)) {
                break;
            }
        }
        for (int i = 0; i < bomb.range; i++) {
            if (destroyAt(bomb, grid, players, bomb.x - i, bomb.y)) {
                break;
            }

        }
        for (int i = 0; i < bomb.range; i++) {
            if (destroyAt(bomb, grid, players, bomb.x + i, bomb.y)) {
                break;
            }
        }
    }

    private boolean destroyAt(Bomb bomb, Grid grid, Players players, int x, int y) {
        if (grid.destroyTile(x, y)) {
            return true;
        }
        
        players.killAt(x, y);

        List<Bomb> clone = (List<Bomb>) bombs.clone();
        for (Bomb otherBomb : clone) {
            if (otherBomb != bomb
                    && otherBomb.x == x
                    && otherBomb.y == y) {
                explodeBomb(otherBomb, grid, players);
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
