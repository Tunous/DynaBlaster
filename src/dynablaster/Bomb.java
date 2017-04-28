package dynablaster;

public class Bomb {

    private final Player owner;
    public final long placementTime;
    public final int x;
    public final int y;

    public Bomb(Player owner, int x, int y) {
        this.owner = owner;
        this.x = x;
        this.y = y;
        placementTime = System.currentTimeMillis();
        
        owner.placedBomb();
    }

    public boolean shouldExplode() {
        return System.currentTimeMillis() - placementTime >= 2000;
    }
    
    public void exploded() {
        owner.restoredBomb();
    }
}
