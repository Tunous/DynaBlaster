package dynablaster;

public enum Tile {
    /**
     * Regular tile that can be freely walked on.
     */
    GRASS,
    /**
     * Indestructible tile.
     * <p>
     * <ul>
     * <li>The player is NOT allowed to walk on this tile.</li>
     * <li>Bombs are NOT able to destroy this tile.</li>
     * </ul>
     * </p>
     */
    INDESTRUCTIBLE,
    /**
     * Destructible tile.
     * <p>
     * <ul>
     * <li>The player is NOT allowed to walk on this tile.</li>
     * <li>Bombs are able to destroy this tile.</li>
     * </ul>
     * </p>
     */
    DESTRUCTIBLE,
    /**
     * A bomb power-up.
     * <p>
     * When collected a player gets an additional bomb.
     * </p>
     */
    POWERUP_BOMB,
    /**
     * A range increase power-up.
     * <p>
     * When collected the range of player's bombs is increased by one tile in
     * every direction.
     * </p>
     */
    POWERUP_RANGE
}
