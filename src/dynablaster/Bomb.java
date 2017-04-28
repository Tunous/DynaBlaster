package dynablaster;

public class Bomb {
    public final long placementTime;
    public final int x;
    public final int y;

    public Bomb(int x, int y) {
        this.x = x;
        this.y = y;
        placementTime = System.currentTimeMillis();
    }
    
    public boolean shouldExplode() {
        return System.currentTimeMillis() - placementTime >= 2000;
    }
}
