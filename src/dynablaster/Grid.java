package dynablaster;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.util.Random;

public class Grid {

    public static final int TILE_SIZE = 16;
    public static final int WIDTH = 13;
    public static final int HEIGHT = 13;

    private static final int TILES = WIDTH * HEIGHT;

    private final Tile tiles[] = new Tile[TILES];

    private final Image indestructible;
    private final Image grass;
    private final Image grassShadow;
    private final Image destructible;

    public Grid() {
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        indestructible = toolkit.getImage("indestructible.png");
        grass = toolkit.getImage("grass.png");
        grassShadow = toolkit.getImage("grass-shadow.png");
        destructible = toolkit.getImage("destructible.png");

        generateGrid();
    }

    private void generateGrid() {
        Random random = new Random();
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < WIDTH; y++) {
                Tile tile;
                if (x == 0 || y == 0 || x == WIDTH - 1 || y == HEIGHT - 1) {
                    // Game border
                    tile = Tile.INDESTRUCTIBLE;
                } else if (x % 2 == 0 && y % 2 == 0) {
                    // Middle columns
                    tile = Tile.INDESTRUCTIBLE;
                } else if (!isLockedPoint(x, y) && random.nextInt(3) != 0) {
                    tile = Tile.DESTRUCTIBLE;
                } else {
                    // Grass
                    tile = Tile.GRASS;
                }

                setTile(x, y, tile);
            }
        }
    }

    private boolean isLockedPoint(int x, int y) {
        return x == 1 && y == 1
                || x == 2 && y == 1
                || x == 1 && y == 2
                || x == WIDTH - 2 && y == WIDTH - 2
                || x == WIDTH - 3 && y == WIDTH - 2
                || x == WIDTH - 2 && y == WIDTH - 3;
    }

    public final void setTile(int x, int y, Tile tile) {
        setTile(x + y * WIDTH, tile);
    }

    public final void setTile(int pos, Tile tile) {
        tiles[pos] = tile;
    }

    public Tile getTile(int x, int y) {
        return getTile(x + y * WIDTH);
    }

    public Tile getTile(int pos) {
        return tiles[pos];
    }

    public boolean canMoveTo(int x, int y) {
        if (!isValidPosition(x, y)) {
            return false;
        }

        final Tile tile = getTile(x, y);
        return tile != Tile.INDESTRUCTIBLE && tile != Tile.DESTRUCTIBLE;
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && y >= 0 && x < WIDTH && y < HEIGHT;
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        for (int x = 0; x < 13; x++) {
            for (int y = 0; y < 13; y++) {
                Image tileImage;
                if (getTile(x, y) == Tile.INDESTRUCTIBLE) {
                    tileImage = indestructible;
                } else if (getTile(x, y) == Tile.DESTRUCTIBLE) {
                    tileImage = destructible;
                } else if (y > 0 && !canMoveTo(x, y - 1)) {
                    tileImage = grassShadow;
                } else {
                    tileImage = grass;
                }
                g.drawImage(tileImage, x * TILE_SIZE, y * TILE_SIZE, observer);
            }
        }
    }

    public boolean destroyTile(int x, int y) {
        if (!isValidPosition(x, y)) {
            return false;
        }

        if (getTile(x, y) == Tile.DESTRUCTIBLE) {
            setTile(x, y, Tile.GRASS);
            return true;
        }
        return false;
    }
}
