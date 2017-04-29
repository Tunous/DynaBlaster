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
    private final GameController controller;

    public Bombs(GameController controller) {
        this.controller = controller;
        
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        bombImage = toolkit.getImage("bomb.png");
    }
    
    public void newGame() {
        bombs.clear();
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

    public void update() {
        synchronized (bombs) {
            List<Bomb> clone = (List<Bomb>) bombs.clone();
            for (Bomb bomb : clone) {
                if (bomb.shouldExplode()) {
                    explodeBomb(bomb);
                }
            }
        }
    }

    private void explodeBomb(Bomb bomb) {
        if (bomb.hasExploded()) {
            return;
        }

        bomb.exploded();
        bombs.remove(bomb);

        for (int i = 0; i < bomb.range; i++) {
            if (destroyAt(bomb, bomb.x, bomb.y - i)) {
                break;
            }
        }
        for (int i = 0; i < bomb.range; i++) {
            if (destroyAt(bomb, bomb.x, bomb.y + i)) {
                break;
            }
        }
        for (int i = 0; i < bomb.range; i++) {
            if (destroyAt(bomb, bomb.x - i, bomb.y)) {
                break;
            }

        }
        for (int i = 0; i < bomb.range; i++) {
            if (destroyAt(bomb, bomb.x + i, bomb.y)) {
                break;
            }
        }
    }

    private boolean destroyAt(Bomb bomb, int x, int y) {
        if (controller.grid.destroyTile(x, y)) {
            return true;
        }
        
        controller.players.killAt(x, y);

        List<Bomb> clone = (List<Bomb>) bombs.clone();
        for (Bomb otherBomb : clone) {
            if (otherBomb != bomb
                    && otherBomb.x == x
                    && otherBomb.y == y) {
                explodeBomb(otherBomb);
                return true;
            }
        }

        return false;
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        final int width = bombImage.getWidth(observer) * 2;
        final int height = bombImage.getHeight(observer) * 2;
        
        synchronized (bombs) {
            for (Bomb bomb : bombs) {
                g.drawImage(bombImage,
                        bomb.x * Grid.TILE_SIZE,
                        bomb.y * Grid.TILE_SIZE,
                        width,
                        height,
                        observer);
            }
        }
    }

    public boolean isBombAt(int x, int y) {
        synchronized (bombs) {
            for (Bomb bomb : bombs) {
                if (bomb.x == x && bomb.y == y) {
                    return true;
                }
            }
        }
        
        return false;
    }
}
