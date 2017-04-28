package dynablaster;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;

public class Grid {
    public static final int TILE_SIZE = 16;
    public static final int WIDTH = 13;
    public static final int HEIGHT = 13;
    
    private static final int TILES = WIDTH * HEIGHT;
    
    private final Tile tiles[] = new Tile[TILES];
    
    private final Image indestructible;
    private final Image bomb;
    private final Image grass;
    private final Image grassShadow;

    public Grid() {
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        indestructible = toolkit.getImage("indestructible.png");
        bomb = toolkit.getImage("bomb.png");
        grass = toolkit.getImage("grass.png");
        grassShadow = toolkit.getImage("grass-shadow.png");
        
        generateGrid();
    }

    private void generateGrid() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < WIDTH; y++) {
                Tile tile;
                if (x == 0 || y == 0 || x == WIDTH - 1 || y == HEIGHT - 1) {
                    // Game border
                    tile = Tile.INDESTRUCTIBLE;
                } else if (x % 2 == 0 && y % 2 == 0) {
                    // Middle columns
                    tile = Tile.INDESTRUCTIBLE;
                } else {
                    // Grass
                    tile = Tile.GRASS;
                }
                
                setTile(x, y, tile);
            }
        }
        
        setTile(3, 2, Tile.INDESTRUCTIBLE);
        setTile(3, 3, Tile.INDESTRUCTIBLE);
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
        if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT) {
            return false;
        }
        return getTile(x, y) != Tile.INDESTRUCTIBLE;
    }
    
    public void draw(Graphics2D g, ImageObserver observer) {
        for (int x = 0; x < 13; x++) {
            for (int y = 0; y < 13; y++) {
                Image tileImage;
                if (getTile(x, y) == Tile.INDESTRUCTIBLE) {
                    tileImage = indestructible;
                } else if (y > 0 && getTile(x, y - 1) == Tile.INDESTRUCTIBLE) {
                    tileImage = grassShadow;
                } else {
                    tileImage = grass;
                }
                g.drawImage(tileImage, x * TILE_SIZE, y * TILE_SIZE, observer);

                Image image = null;
                switch (getTile(x, y)) {
                    case BOMB:
                        image = bomb;
                        break;
                    default:
                        break;
                }
                if (image != null) {
                    g.drawImage(image, x * Grid.TILE_SIZE, y * Grid.TILE_SIZE, observer);
                }
            }
        }
    }
}
