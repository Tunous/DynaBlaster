package dynablaster;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa odpowiedzialna za zarządzanie wszystkimi bombami.
 */
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
        bombImage = toolkit.getImage("res/bomb.png");
        explosionImage = toolkit.getImage("res/explosion.png");
        tileExplosionImage = toolkit.getImage("res/explosion-tile.png");
    }

    /**
     * Czyści wszystkie bomby i eksplozje aby można było rozpocząć nową grę.
     */
    public void newGame() {
        bombs.clear();
        explosions.clear();
    }

    /**
     * Ustawia nową bombę na podanej pozycji.
     *
     * @param player Gracz który będzie właścicielem bomby.
     * @param x Współrzędna x pozycji bomby.
     * @param y Współrzędna y pozycji bomby.
     */
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

    public void update() {
        detonateAllBombs();
        removeTimedOutExplosions();
    }

    /**
     * Zwraca czy gracz może wejść na pozycję o danych współrzędnych.
     *
     * @param x Współrzędna x.
     * @param y Współrzędna y.
     * @return {@code true} jeśli gracz może wejść na daną pozycję.
     */
    public boolean canMoveTo(int x, int y) {
        if (isBombAt(x, y)) {
            return false;
        }

        for (IExplosion explosion : explosions) {
            // Gracz nie może wchodzić na wybuchające ściany.
            if (explosion instanceof TileExplosion
                    && explosion.isInRange(x, y)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Zwraca czy podany gracz znajduje się na pozycji objętej działaniem
     * jakiejkolwiek eksplozji.
     *
     * @param player Gracz dla którego pozycji dokonać sprawdzenia.
     * @return {@code true} jeśli gracz znajduje się w zasięgu eksplozji.
     */
    public boolean hasEnteredExplosion(Player player) {
        for (IExplosion explosion : explosions) {
            if (explosion.isInRange(player.getX(), player.getY())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Detonuje wszystkie bomby.
     */
    private void detonateAllBombs() {
        List<Bomb> currentBombs = (List<Bomb>) bombs.clone();
        for (Bomb bomb : currentBombs) {
            if (bomb.shouldExplode()) {
                detonateBomb(bomb, currentBombs);
            }
        }
    }

    /**
     * Zwraca czy na podanych współrzędnych znajduję się aktualnie jakaś bomba.
     *
     * @param x Współrzędna x bomby.
     * @param y Współrzędna y bomby.
     */
    private boolean isBombAt(int x, int y) {
        for (Bomb bomb : bombs) {
            if (bomb.x == x && bomb.y == y) {
                return true;
            }
        }

        return false;
    }

    /**
     * Usuwa zakończone eksplozje.
     */
    private void removeTimedOutExplosions() {
        List<IExplosion> explosionsClone = (List<IExplosion>) explosions.clone();
        for (IExplosion explosion : explosionsClone) {
            if (explosion.hasTimedOut()) {
                explosions.remove(explosion);
            }
        }
    }

    /**
     * Detonuje wskazaną bombę powodując wysadzenie wszystkiego co jest w
     * zasięgu jej eksplozji.
     *
     * @param bomb Bomba do zdetonowania.
     * @param currentBombs Lista bomb jakie znajdowały się planszy w przed
     * detonacją.
     */
    private void detonateBomb(Bomb bomb, List<Bomb> currentBombs) {
        if (bomb.hasExploded()) {
            return;
        }

        // Stwórz eksplozję która zabije graczy którzy na nią wejdą w czasie
        // działania wybuchu.
        long now = System.currentTimeMillis();
        Explosion explosion = new Explosion(bomb.x, bomb.y, bomb.range, now);
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
     * Niszczy wszystko co jest zlokalizowane na podanych współrzędnych.
     *
     * @param x Współrzędna x.
     * @param y Współrzędna y.
     * @return {@code true} jeśli coś zostało zniszczone.
     */
    private boolean destroyAt(int x, int y, long time,
            List<Bomb> currentBombs) {
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
