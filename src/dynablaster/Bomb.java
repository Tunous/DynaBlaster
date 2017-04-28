package dynablaster;

public class Bomb {

    private final Player owner;

    private boolean hasExploded = false;

    public final long placementTime;
    public final int x;
    public final int y;
    public final int range;

    public Bomb(Player owner, int x, int y, int range) {
        this.owner = owner;
        this.x = x;
        this.y = y;
        this.range = range;
        placementTime = System.currentTimeMillis();
    }

    public boolean shouldExplode() {
        return System.currentTimeMillis() - placementTime >= 2000;
    }

    public void exploded() {
        hasExploded = true;
        owner.restoredBomb();
    }

    public boolean hasExploded() {
        return hasExploded;
    }
}
