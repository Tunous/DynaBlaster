package dynablaster;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bombs {

    private final ArrayList<Bomb> bombs = new ArrayList<>();
    private final ArrayList<Explosion> explosions = new ArrayList<>();

    private final Image bombImage;
    private final GameController controller;
    private final Image explosionImage;

    public Bombs(GameController controller) {
        this.controller = controller;

        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        bombImage = toolkit.getImage("bomb.png");
        explosionImage = toolkit.getImage("explosion.png");
    }

    public void newGame() {
        bombs.clear();
        explosions.clear();
    }

    public void placeBomb(Player player, int x, int y) {
        if (!player.canPlaceBombs()) {
            return;
        }

        for (Bomb bomb : bombs) {
            if (bomb.x == x && bomb.y == y) {
                return;
            }
        }

        bombs.add(player.placeBomb(x, y));
    }

    public void update() {
        List<Bomb> clone = (List<Bomb>) bombs.clone();
        for (Bomb bomb : clone) {
            if (bomb.shouldExplode()) {
                explodeBomb(bomb);
            }
        }
        long now = System.currentTimeMillis();

        List<Explosion> explosionsClone = (List<Explosion>) explosions.clone();
        for (Explosion explosion : explosionsClone) {
            if (now - explosion.placementTime >= Explosion.DURATION) {
                explosions.remove(explosion);
            }
        }
    }

    private void explodeBomb(Bomb bomb) {
        if (bomb.hasExploded()) {
            return;
        }

        long time = System.currentTimeMillis();
        final Explosion explosion = new Explosion(bomb.x, bomb.y, time);

        explosions.add(explosion);
        bomb.exploded();
        bombs.remove(bomb);

        if (destroyAt(bomb, bomb.x, bomb.y)) {
            return;
        }

        for (int i = 1; i <= bomb.range; i++) {
            if (destroyAt(bomb, bomb.x, bomb.y - i)) {
                break;
            }
            explosion.rangeUp += 1;
        }
        for (int i = 1; i <= bomb.range; i++) {
            if (destroyAt(bomb, bomb.x, bomb.y + i)) {
                break;
            }
            explosion.rangeDown += 1;
        }
        for (int i = 1; i <= bomb.range; i++) {
            if (destroyAt(bomb, bomb.x - i, bomb.y)) {
                break;
            }
            explosion.rangeLeft += 1;
        }
        for (int i = 1; i <= bomb.range; i++) {
            if (destroyAt(bomb, bomb.x + i, bomb.y)) {
                break;
            }
            explosion.rangeRight += 1;
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
        final int width = bombImage.getWidth(observer) * Grid.SCALE;
        final int height = bombImage.getHeight(observer) * Grid.SCALE;

        for (Bomb bomb : bombs) {
            g.drawImage(bombImage,
                    bomb.x * Grid.TILE_SIZE,
                    bomb.y * Grid.TILE_SIZE,
                    width,
                    height,
                    observer);
        }

        for (Explosion explosion : explosions) {
            explosion.draw(g, observer, explosionImage);
        }
    }

    public boolean isBombAt(int x, int y) {
        for (Bomb bomb : bombs) {
            if (bomb.x == x && bomb.y == y) {
                return true;
            }
        }

        return false;
    }

    public boolean enteredExplosion(Player player) {
        for (Explosion explosion : explosions) {
            if (explosion.isInRange(player.getTileX(), player.getTileY())) {
                return true;
            }
        }
        return false;
    }
}
