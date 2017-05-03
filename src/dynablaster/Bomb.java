package dynablaster;

import java.util.concurrent.TimeUnit;

public class Bomb {

    private static final long TIME_TO_EXPLOSION = TimeUnit.SECONDS.toMillis(2);

    /**
     * The player which placed this bomb.
     */
    private final Player owner;

    /**
     * Tells whether the bomb has already exploded.
     */
    private boolean hasExploded = false;

    /**
     * The time when this bomb has been placed.
     */
    public final long when;

    /**
     * The horizontal coordinate of the bomb position. (In tiles)
     */
    public final int x;

    /**
     * The vertical coordinate of the bomb position. (In tiles)
     */
    public final int y;

    /**
     * The distance (in tiles) which the bomb can reach with its explosion.
     */
    public final int range;

    public Bomb(Player owner, int x, int y, int range) {
        this.owner = owner;
        this.x = x;
        this.y = y;
        this.range = range;

        when = System.currentTimeMillis();
    }

    /**
     * Tells whether the bomb should explode.
     *
     * @return {@code true} if the bomb should explode; otherwise,
     * {@code false}.
     */
    public boolean shouldExplode() {
        return System.currentTimeMillis() - when >= TIME_TO_EXPLOSION;
    }

    /**
     * Marks this bomb as exploded.
     */
    public void setAsExploded() {
        hasExploded = true;
        owner.addBomb();
    }

    /**
     * Tells whether the bomb has already exploded.
     *
     * @return {@code true} if the bomb has exploded; otherwise {@code false}.
     */
    public boolean hasExploded() {
        return hasExploded;
    }
}
