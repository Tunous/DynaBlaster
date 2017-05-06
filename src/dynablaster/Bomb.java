package dynablaster;

import java.util.concurrent.TimeUnit;

/**
 * Klasa reprezentująca bombę postawioną na planszy.
 */
public class Bomb {

    /**
     * Czas przez jaki bomba jest postawiona zanim wybuchnie.
     */
    private static final long TIME_TO_EXPLOSION = TimeUnit.SECONDS.toMillis(2);

    /**
     * Gracz który postawił tą bombę.
     */
    private final Player owner;

    /**
     * Oznacza czy bomba już eksplodowała.
     */
    private boolean hasExploded = false;

    /**
     * Czas (w millisekundach) kiedy bomba została postawiona.
     */
    public final long when;

    /**
     * Współrzędna x pozycji bomby.
     */
    public final int x;

    /**
     * Współrzędna y pozycji bomby.
     */
    public final int y;

    /**
     * Dystans jaki osiąga eksplozja tej bomby.
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
     * Zwraca czy bomba powinna już eksplodować.
     *
     * @return {@code true} jeśli bomba powinna eksplodować.
     */
    public boolean shouldExplode() {
        return System.currentTimeMillis() - when >= TIME_TO_EXPLOSION;
    }

    /**
     * Oznacza bombę jako eksplodowaną.
     */
    public void setAsExploded() {
        if (hasExploded) {
            return;
        }

        hasExploded = true;
        owner.addBomb();
    }

    /**
     * Zwraca czy bomba już eksplodowała.
     *
     * @return {@code true} jeśli bomba eksplodowała.
     */
    public boolean hasExploded() {
        return hasExploded;
    }
}
