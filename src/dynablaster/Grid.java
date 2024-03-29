package dynablaster;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.util.Random;

public class Grid {

    private static final int WIDTH = 13;
    private static final int HEIGHT = 13;
    private static final int TILES = WIDTH * HEIGHT;
    private static final Random RANDOM = new Random();

    public static final int SCALE = 2;
    public static final int TILE_SIZE = 16 * SCALE;
    public static final Dimension SIZE
            = new Dimension(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);

    private final Tile tiles[] = new Tile[TILES];

    private final Image indestructible;
    private final Image grass;
    private final Image grassShadow;
    private final Image destructible;
    private final Image powerupBombImage;
    private final Image powerupRangeImage;

    private final GameController controller;

    public Grid(GameController controller) {
        this.controller = controller;

        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        indestructible = toolkit.getImage("res/indestructible.png");
        grass = toolkit.getImage("res/grass.png");
        grassShadow = toolkit.getImage("res/grass-shadow.png");
        destructible = toolkit.getImage("res/destructible.png");
        powerupBombImage = toolkit.getImage("res/powerup-bomb.png");
        powerupRangeImage = toolkit.getImage("res/powerup-range.png");
        
        generateGrid();
    }

    public void newGame() {
        generateGrid();
    }

    private void generateGrid() {
        Random random = new Random();
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < WIDTH; y++) {
                Tile tile;
                if (x == 0 || y == 0 || x == WIDTH - 1 || y == HEIGHT - 1) {
                    // Ramka
                    tile = Tile.INDESTRUCTIBLE;
                } else if (x % 2 == 0 && y % 2 == 0) {
                    // Wewnętrzne kolumny
                    tile = Tile.INDESTRUCTIBLE;
                } else if (x > 1 && x < WIDTH - 2 && y > 1 && y < HEIGHT - 2
                        && (x % 2 == 0 || y % 2 == 0)
                        && random.nextInt(6) == 0) {
                    // Losowe niezniszczlne blokady
                    tile = Tile.INDESTRUCTIBLE;
                } else if (!isLockedPoint(x, y) && random.nextInt(3) != 0) {
                    tile = Tile.DESTRUCTIBLE;
                } else {
                    tile = Tile.GRASS;
                }

                setTile(x, y, tile);
            }
        }
    }

    private boolean isLockedPoint(int x, int y) {
        final boolean whitePlayerSpawn = x == 1 && y == 1
                || x == 2 && y == 1
                || x == 1 && y == 2;
        final boolean greenPlayerSpawn = x == WIDTH - 2 && y == HEIGHT - 2
                || x == WIDTH - 3 && y == HEIGHT - 2
                || x == WIDTH - 2 && y == HEIGHT - 3;
        final boolean redPlayerSpawn = x == WIDTH - 2 && y == 1
                || x == WIDTH - 3 && y == 1
                || x == WIDTH - 2 && y == 2;
        final boolean bluePlayerSpawn = x == 1 && y == HEIGHT - 2
                || x == 2 && y == HEIGHT - 2
                || x == 1 && y == HEIGHT - 3;
        return whitePlayerSpawn || greenPlayerSpawn || redPlayerSpawn
                || bluePlayerSpawn;
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

        if (isSolidBlockAt(x, y)) {
            return false;
        }

        return controller.bombs.canMoveTo(x, y);
    }

    private boolean isSolidBlockAt(int x, int y) {
        final Tile tile = getTile(x, y);
        return tile == Tile.INDESTRUCTIBLE || tile == Tile.DESTRUCTIBLE;
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && y >= 0 && x < WIDTH && y < HEIGHT;
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        for (int x = 0; x < 13; x++) {
            for (int y = 0; y < 13; y++) {
                Tile tile = getTile(x, y);
                Image tileImage;

                switch (tile) {
                    case INDESTRUCTIBLE:
                        tileImage = indestructible;
                        break;
                    case DESTRUCTIBLE:
                        tileImage = destructible;
                        break;
                    case POWERUP_BOMB:
                        tileImage = powerupBombImage;
                        break;
                    case POWERUP_RANGE:
                        tileImage = powerupRangeImage;
                        break;

                    default:
                        final boolean isUnderSolidBlock
                                = y > 0 && isSolidBlockAt(x, y - 1);
                        tileImage = isUnderSolidBlock ? grassShadow : grass;
                        break;
                }

                g.drawImage(tileImage,
                        x * TILE_SIZE,
                        y * TILE_SIZE,
                        TILE_SIZE,
                        TILE_SIZE,
                        observer);
            }
        }
    }

    public Tile destroyTile(int x, int y) {
        if (!isValidPosition(x, y)) {
            return Tile.GRASS;
        }
        
        final Tile tile = getTile(x, y);

        if (tile == Tile.DESTRUCTIBLE) {
            Tile newTile = Tile.GRASS;
            if (RANDOM.nextInt(5) == 0) {
                newTile = RANDOM.nextInt(2) == 0
                        ? Tile.POWERUP_BOMB : Tile.POWERUP_RANGE;
            }

            setTile(x, y, newTile);
            return Tile.DESTRUCTIBLE;
        }
        
        // Destroy any hit powerups
        if (tile == Tile.POWERUP_BOMB || tile == Tile.POWERUP_RANGE) {
            setTile(x, y, Tile.GRASS);
        }

        return tile;
    }

    public void collectPowerup(Player player) {
        if (player.isDead()) {
            return;
        }
        
        int x = player.getX();
        int y = player.getY();

        Tile tile = getTile(x, y);

        switch (tile) {
            case POWERUP_BOMB:
                player.addBomb();
                setTile(x, y, Tile.GRASS);
                break;
            case POWERUP_RANGE:
                player.increaseRange();
                setTile(x, y, Tile.GRASS);
                break;
            default:
                break;
        }
    }
}
