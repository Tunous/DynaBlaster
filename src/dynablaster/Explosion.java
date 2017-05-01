package dynablaster;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;

public class Explosion {

    public final int x;
    public final int y;
    public final long placementTime;

    public Explosion(int x, int y, long placementTime) {
        this.y = y;
        this.x = x;
        this.placementTime = placementTime;
    }

    public int rangeUp = 0;
    public int rangeDown = 0;
    public int rangeLeft = 0;
    public int rangeRight = 0;

    public void draw(Graphics2D g, ImageObserver observer, Image image) {
        drawPart(g, observer, image, Direction.NONE, false, 0, 0);

        for (int i = 1; i <= rangeUp; i++) {
            drawPart(g, observer, image, Direction.UP, i == rangeUp, 0, -i);
        }
        for (int i = 1; i <= rangeDown; i++) {
            drawPart(g, observer, image, Direction.DOWN, i == rangeDown, 0, i);
        }
        for (int i = 1; i <= rangeLeft; i++) {
            drawPart(g, observer, image, Direction.LEFT, i == rangeLeft, -i, 0);
        }
        for (int i = 1; i <= rangeRight; i++) {
            drawPart(g, observer, image, Direction.RIGHT, i == rangeRight, i, 0);
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