package dynablaster;

public class Player {

    private int x;
    private int y;
    private int speed = Grid.SCALE;
    private int bombs = 5;
    private int bombRange = 5;
    private boolean dead = false;
    private Direction movementDirection = Direction.NONE;

    public final PlayerColor color;

    public Player(PlayerColor color, int spawnX, int spawnY) {
        this.color = color;

        x = spawnX * Grid.TILE_SIZE;
        y = spawnY * Grid.TILE_SIZE;
    }

    public int getTileX() {
        return (x + Grid.TILE_SIZE / 2) / Grid.TILE_SIZE + 1;
    }

    public int getTileY() {
        return (y + Grid.TILE_SIZE / 2) / Grid.TILE_SIZE + 1;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setMovementDirection(Direction dir) {
        movementDirection = dir;
    }

    public void increaseSpeed() {
        speed += Grid.SCALE;
    }

    public Bomb placeBomb(int bombX, int bombY) {
        if (!canPlaceBombs()) {
            return null;
        }

        Bomb bomb = new Bomb(this, bombX, bombY, bombRange);
        bombs -= 1;
        return bomb;
    }

    public void restoredBomb() {
        bombs += 1;
    }

    public boolean canPlaceBombs() {
        return bombs > 0;
    }

    public void kill() {
        dead = true;
    }

    public boolean isDead() {
        return dead;
    }

    public void update(Grid grid) {
        if (movementDirection == Direction.NONE) {
            return;
        }

        int modX = x % Grid.TILE_SIZE;
        int modY = y % Grid.TILE_SIZE;

        alignAndMove(grid, getTileX(), getTileY(), modX, modY);
    }

    /**
     * Align the player on the grid and move once aligned.
     *
     * @param grid The game grid used to check for collisions.
     * @param tileX The x position of the player, in tiles.
     * @param tileY The y position of the player, in tiles.
     * @param modX The horizontal miss-alignment of the player.
     * @param modY The vertical miss-alignment of the player.
     */
    private void alignAndMove(Grid grid, int tileX, int tileY, int modX,
            int modY) {
        if (canMoveStraight(grid, tileX, tileY)) {
            // - Align the player in axis opposite to the movement direction
            // - Once aligned start moving
            align(!movementDirection.isHorizontal(), false, true);
            return;
        }

        int mod = movementDirection.isHorizontal() ? modY : modX;

        final boolean canMoveToLeftSide = mod >= Grid.TILE_SIZE / 2
                && canMoveToSide(grid, tileX, tileY, Direction.LEFT);
        final boolean canMoveToRightSide = mod < Grid.TILE_SIZE / 2
                && mod != 0
                && canMoveToSide(grid, tileX, tileY, Direction.RIGHT);

        // Player can't move in a straight line but is not centered and can be
        // pushed to one side to start moving in a straight line
        if (canMoveToLeftSide || canMoveToRightSide) {
            // - Push the player to the block on the side
            // - Once pushed and aligned start moving
            align(!movementDirection.isHorizontal(), true, true);
            return;
        }

        // Only align the player on direction of the movement.
        // This makes sure that the player always moves as far as possible,
        // even with high movement speed.
        align(movementDirection.isHorizontal(), false, false);
    }

    /**
     * Returns whether the player can move through the block located in front of
     * him.
     *
     * @param grid The game grid used to check for collisions.
     * @param tileX The x position of the player, in tiles.
     * @param tileY The y position of the player, in tiles.
     * @return <c>true</c> if the player can move; otherwise, <c>false</c>.
     */
    private boolean canMoveStraight(Grid grid, int tileX, int tileY) {
        switch (movementDirection) {
            case UP:
                tileY -= 1;
                break;
            case DOWN:
                tileY += 1;
                break;
            case LEFT:
                tileX -= 1;
                break;
            case RIGHT:
                tileX += 1;
                break;
        }

        return grid.canMoveTo(tileX, tileY);
    }

    /**
     * Returns whether the player can move through the block located next to the
     * block in front of him.
     *
     * @param grid The game grid used to check for collisions.
     * @param tileX The x position of the player, in tiles.
     * @param tileY The y position of the player, in tiles.
     * @param side If <c>Direction.RIGHT</c> check the block located in
     * clockwise direction to the block in front of the player. Otherwise check
     * the block in the counter-clockwise direction.
     * @return <c>true</c> if the player can move; otherwise, <c>false</c>.
     */
    private boolean canMoveToSide(Grid grid, int tileX, int tileY,
            Direction side) {

        // Side must be a horizontal direction
        assert side.isHorizontal();

        boolean checkClockwise = side == Direction.RIGHT;

        switch (movementDirection) {
            case UP:
                tileX += checkClockwise ? 1 : -1;
                tileY -= 1;
                break;
            case DOWN:
                tileX += checkClockwise ? 1 : -1;
                tileY += 1;
                break;
            case LEFT:
                tileX -= 1;
                tileY += checkClockwise ? 1 : -1;
                break;
            case RIGHT:
                tileX += 1;
                tileY += checkClockwise ? 1 : -1;
                break;
        }

        return grid.canMoveTo(tileX, tileY);
    }

    /**
     * Align the player to the grid and start moving if possible.
     *
     * @param horizontal If <c>true</c> move the player horizontally; otherwise
     * move him vertically.
     * @param reverse If <c>true</c> push the player away from his current
     * position; otherwise align him to the center of the tile.
     * @param moveIfAligned Whether the player should move once aligned.
     */
    private void align(boolean horizontal, boolean reverse,
            boolean moveIfAligned) {
        int pos = horizontal ? x : y;
        int mod = pos % Grid.TILE_SIZE;

        if (mod == 0) {
            if (moveIfAligned) {
                moveInternal();
            }
            return;
        }

        int alignSpeed = Math.min(mod, speed);

        if (!reverse && mod < Grid.TILE_SIZE / 2
                || reverse && mod >= Grid.TILE_SIZE / 2) {
            if (horizontal) {
                x -= alignSpeed;
            } else {
                y -= alignSpeed;
            }
        } else {
            if (horizontal) {
                x += alignSpeed;
            } else {
                y += alignSpeed;
            }
        }
    }

    /**
     * Move the player without performing any checks.
     */
    private void moveInternal() {
        switch (movementDirection) {
            case UP:
                y -= speed;
                break;
            case DOWN:
                y += speed;
                break;
            case LEFT:
                x -= speed;
                break;
            case RIGHT:
                x += speed;
                break;
            default:
                break;
        }
    }
}
