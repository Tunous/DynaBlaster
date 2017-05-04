package dynablaster;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;

public class TileExplosion implements IExplosion {
    
    private static final int DURATION = 700;

    public final int x;
    public final int y;
    public final long when;

    public TileExplosion(int x, int y, long when) {
        this.x = x;
        this.y = y;
        this.when = when;
    }
    
    @Override
    public boolean isInRange(int x, int y) {
        return this.x == x && this.y == y;
    }

    @Override
    public boolean hasTimedOut() {
        return (System.currentTimeMillis() - when) >= DURATION;
    }
    
    @Override
    public void draw(Graphics2D g, ImageObserver observer, Image image) {
        int targetX = x * Grid.TILE_SIZE;
        int targetY = y * Grid.TILE_SIZE;
        
        int sourceX = 0;
        int sourceY = 0;
        
        long lifeTime = (System.currentTimeMillis() - when) / (DURATION / 7);
        int frame = (int) (lifeTime % 7);
        
        sourceX = frame * Grid.TILE_SIZE;
        
        g.drawImage(image,
                targetX,
                targetY,
                targetX + Grid.TILE_SIZE,
                targetY + Grid.TILE_SIZE,
                sourceX,
                sourceY,
                sourceX + 16,
                sourceY + 16,
                observer);
    }
}
