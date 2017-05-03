package dynablaster;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;

public class Explosion {
    
    public static final int DURATION = 400;
    
    private final int maxRange;

    public final int x;
    public final int y;
    public final long when;

    public int rangeUp = 0;
    public int rangeDown = 0;
    public int rangeLeft = 0;
    public int rangeRight = 0;

    public Explosion(int x, int y, int maxRange, long placementTime) {
        this.y = y;
        this.x = x;
        this.maxRange = maxRange;
        this.when = placementTime;
    }
    
    public boolean hasTimedOut() {
        return System.currentTimeMillis() - when >= DURATION;
    }
    
    public boolean isInRange(int x, int y) {
        if (this.x != x && this.y != y) {
            // If both x and y axis are different then there is no chance that
            // this tile is in range of the bomb.
            return false;
        }
        
        if (this.x == x && this.y == y) {
            return true;
        }
        
        for (int i = 1; i <= rangeUp; i++) {
            if (this.x == x && this.y - i == y) {
                return true;
            }
        }
        for (int i = 1; i <= rangeDown; i++) {
            if (this.x == x && this.y + i == y) {
                return true;
            }
        }
        for (int i = 1; i <= rangeLeft; i++) {
            if (this.x - i == x && this.y == y) {
                return true;
            }
        }
        for (int i = 1; i <= rangeRight; i++) {
            if (this.x + i == x && this.y == y) {
                return true;
            }
        }
        
        return false;
    }

    public void draw(Graphics2D g, ImageObserver observer, Image image) {
        drawPart(g, observer, image, Direction.NONE, false, 0, 0);

        for (int i = 1; i <= rangeUp; i++) {
            drawPart(g, observer, image, Direction.UP, i == maxRange, 0, -i);
        }
        for (int i = 1; i <= rangeDown; i++) {
            drawPart(g, observer, image, Direction.DOWN, i == maxRange, 0, i);
        }
        for (int i = 1; i <= rangeLeft; i++) {
            drawPart(g, observer, image, Direction.LEFT, i == maxRange, -i, 0);
        }
        for (int i = 1; i <= rangeRight; i++) {
            drawPart(g, observer, image, Direction.RIGHT, i == maxRange, i, 0);
        }
    }

    private void drawPart(Graphics2D g, ImageObserver observer, Image image,
            Direction dir, boolean isEnd, int x2, int y2) {
        
        int offset = 0;
        if (!isEnd && dir != Direction.NONE) {
            switch (dir) {
                case UP:
                case DOWN:
                    offset = 16;
                    break;
                case LEFT:
                case RIGHT:
                    offset = 20;
                    break;
            }
        } else {
            switch (dir) {
                case RIGHT:
                    offset = 4;
                    break;
                case DOWN:
                    offset = 8;
                    break;
                case LEFT:
                    offset = 12;
                    break;
                case NONE:
                    offset = 24;
                    break;
            }
        }
        
        long life = (System.currentTimeMillis() - when);
        int frame = (int) ((life * 4) / DURATION);
        offset += frame;
        
        final int targetX = (x + x2) * Grid.TILE_SIZE;
        final int targetY = (y + y2) * Grid.TILE_SIZE;
        final int eWidth = 16;
        final int eHeight = 16;

        final int offsetX = offset % 20;
        final int offsetY = offset / 20;

        final int sourceX = offsetX * eWidth;
        final int sourceY = offsetY * eHeight;

        g.drawImage(image,
                targetX,
                targetY,
                targetX + eWidth * Grid.SCALE,
                targetY + eHeight * Grid.SCALE,
                sourceX,
                sourceY,
                sourceX + eWidth,
                sourceY + eHeight,
                observer);
    }
}
