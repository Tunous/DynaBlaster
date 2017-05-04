package dynablaster;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

public class Bombs {

    private final ArrayList<Bomb> bombs = new ArrayList<>();
    private final ArrayList<IExplosion> explosions = new ArrayList<>();

    private final Image bombImage;
    private final GameController controller;
    private final Image explosionImage;
    private final Image tileExplosionImage;

    public Bombs(GameController controller) {
        this.controller = controller;

        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        bombImage = toolkit.getImage("bomb.png");
        explosionImage = toolkit.getImage("explosion.png");
        tileExplosionImage = toolkit.getImage("explosion-tile.png");
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

    public void draw(Graphics2D g, ImageObserver observer) {
        final int width = 16;
        final int height = 16;

        for (Bomb bomb : bombs) {
            long lifeTime = (System.currentTimeMillis() - bomb.when) / 200;
            int frame = (int) (lifeTime % 3);
            int offsetX = frame * width;

            g.drawImage(bombImage,
                    bomb.x * Grid.TILE_SIZE,
                    bomb.y * Grid.TILE_SIZE,
                    bomb.x * Grid.TILE_SIZE + width * Grid.SCALE,
                    bomb.y * Grid.TILE_SIZE + height * Grid.SCALE,
                    offsetX,
                    0,
                    offsetX + width,
                    height,
                    observer);
        }

        for (IExplosion explosion : explosions) {
            explosion.draw(g, observer,
                    explosion instanceof Explosion ? explosionImage : tileExplosionImage);
        }
    }

    /**
     * Tells whether there is a bomb at the specified tile coordinates.
     *
     * @param x The x tile coordinate.
     * @param y The y tile coordinate.
     * @return {@code true} if there is a bomb at the specified position;
     * otherwise, {@code false}.
     */
    public boolean isBombAt(int x, int y) {
        for (Bomb bomb : bombs) {
            if (bomb.x == x && bomb.y == y) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return whether the specified player has entered area of effect of any
     * explosion.
     *
     * @param player The player for which to perform the check.
     * @return {@code true} if player is standing on explosion; otherwise,
     * {@code false}.
     */
    public boolean hasEnteredExplosion(Player player) {
        for (IExplosion explosion : explosions) {
            if (explosion.isInRange(player.getX(), player.getY())) {
                return true;
            }
        }
        return false;
    }

    public void update() {
        detonateAllBombs();
        removeTimedOutExplosions();
    }

    private void detonateAllBombs() {
        List<Bomb> currentBombs = (List<Bomb>) bombs.clone();
        for (Bomb bomb : currentBombs) {
            if (bomb.shouldExplode()) {
                detonateBomb(bomb, currentBombs);
            }
        }
    }

    private void removeTimedOutExplosions() {
        List<IExplosion> explosionsClone = (List<IExplosion>) explosions.clone();
        for (IExplosion explosion : explosionsClone) {
            if (explosion.hasTimedOut()) {
                explosions.remove(explosion);
            }
        }
    }

    /**
     * Detonate the specified bomb causing everything that is in its range to be
     * destroyed.
     *
     * @param bomb The bomb to detonate.
     */
    private void detonateBomb(Bomb bomb, List<Bomb> currentBombs) {
        if (bomb.hasExploded()) {
            return;
        }

        // Create an explosion indicator that will kill players that walk on it.
        final long now = System.currentTimeMillis();
        final Explosion explosion = new Explosion(bomb.x, bomb.y, bomb.range, now);
        explosions.add(explosion);

        bomb.setAsExploded();
        bombs.remove(bomb);
        controller.players.killAt(bomb.x, bomb.y);

        for (int i = 1; i <= bomb.range; i++) {
            if (destroyAt(bomb.x, bomb.y - i, now, currentBombs)) {
                break;
            }
            explosion.rangeUp += 1;
        }
        for (int i = 1; i <= bomb.range; i++) {
            if (destroyAt(bomb.x, bomb.y + i, now, currentBombs)) {
                break;
            }
            explosion.rangeDown += 1;
        }
        for (int i = 1; i <= bomb.range; i++) {
            if (destroyAt(bomb.x - i, bomb.y, now, currentBombs)) {
                break;
            }
            explosion.rangeLeft += 1;
        }
        for (int i = 1; i <= bomb.range; i++) {
            if (destroyAt(bomb.x + i, bomb.y, now, currentBombs)) {
                break;
            }
            explosion.rangeRight += 1;
        }
    }

    /**
     * Destroy everything that is located at the specified tile coordinates.
     * This includes destroying tiles, detonating other bombs and killing
     * players.
     *
     * @param x The x coordinate of the tile.
     * @param y The y coordinate of the tile.
     * @return {@code true} if something has been destroyed; otherwise,
     * {@code false}.
     */
    private boolean destroyAt(int x, int y, long time, List<Bomb> currentBombs) {
        Tile affectedTile = controller.grid.destroyTile(x, y);
        if (affectedTile == Tile.DESTRUCTIBLE) {
            explosions.add(new TileExplosion(x, y, time));
            return true;
        }
        
        if (affectedTile == Tile.INDESTRUCTIBLE) {
            return true;
        }

        controller.players.killAt(x, y);

        for (Bomb otherBomb : currentBombs) {
            if (otherBomb.x == x && otherBomb.y == y) {
                detonateBomb(otherBomb, currentBombs);
                return true;
            }
        }

        return false;
    }
}
