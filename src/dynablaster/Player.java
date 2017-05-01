package dynablaster;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

public class Player {

    private static final int PLAYER_SIZE = 23;
    private static final int PLAYER_X_OFFSET = 13;
    private static final int PLAYER_Y_OFFSET = 9;

    /**
     * The x coordinate specifying where the player should be drawn on the
     * board. (In pixels)
     */
    private int drawX;

    /**
     * The y coordinate specifying where the player should be drawn on the
     * board. (In pixels)
     */
    private int drawY;

    /**
     * The amount of pixels the player should be moved on each movement action.
     */
    private int speed = Grid.SCALE;

    /**
     * The amount of bomb the player is currently able to place.
     */
    private int bombs = 1;

    /**
     * The maximum explosion range of the bombs placed by the player.
     */
    private int bombRange = 1;

    /**
     * Whether the player is currently dead.
     */
    private boolean dead = false;

    /**
     * The current movement direction of the player. Used to move him around the
     * board every frame.
     */
    private Direction movementDirection = Direction.NONE;

    /**
     * The latest movement direction other than {@code Direction.NONE}. Used to
     * render the player facing correct direction.
     */
    private Direction latestDir = Direction.DOWN;

    /**
     * The color of this player.
     */
    public final PlayerColor color;

    public Player(PlayerColor color, int spawnX, int spawnY) {
        this.color = color;

        drawX = spawnX * Grid.TILE_SIZE;
        drawY = spawnY * Grid.TILE_SIZE;
    }

    /**
     * Get the horizontal position of the player, in tiles.
     *
     * @return The horizontal position of the player, in tiles.
     */
    public int getX() {
        return (drawX + Grid.TILE_SIZE / 2) / Grid.TILE_SIZE + 1;
    }

    /**
     * Get the vertical position of the player, in tiles.
     *
     * @return The vertical position of the player, in tiles.
     */
    public int getY() {
        return (drawY + Grid.TILE_SIZE / 2) / Grid.TILE_SIZE + 1;
    }

    /**
     * Set the current movement direction of the player. If the specified
     * direction is different than {@code Direction.NONE} then the player will
     * move in that direction on every call to the {@code update()} method.
     *
     * @param dir The direction in which the player should move.
     */
    public void setMovementDirection(Direction dir) {
        movementDirection = dir;
        if (dir != Direction.NONE) {
            latestDir = dir;
        }
    }

    /**
     * Increase the movement speed of the player.
     */
    public void increaseSpeed() {
        speed += Grid.SCALE;
    }

    /**
     * Increase the range of the bombs placed by the player by one.
     */
    public void increaseRange() {
        bombRange += 1;
    }

    /**
     * Try to place a bomb at the tile with the specified coordinates.
     *
     * @param bombX The horizontal coordinate of the tile.
     * @param bombY The vertical coordinate of the tile.
     * @return The placed bomb or {@code null} if no bomb was placed.
     */
    public Bomb placeBomb(int bombX, int bombY) {
        if (!canPlaceBombs()) {
            return null;
        }

        Bomb bomb = new Bomb(this, bombX, bombY, bombRange);
        bombs -= 1;
        return bomb;
    }

    /**
     * Increase the amount of bombs that the player can place at the same time
     * by one.
     */
    public void addBomb() {
        bombs += 1;
    }

    /**
     * Tells whether the player is able to place bombs.
     *
     * @return {@code true} if the player can place bombs; otherwise,
     * {@code false}.
     */
    public boolean canPlaceBombs() {
        return bombs > 0;
    }

    /**
     * Kill the player making him stop responding to user input.
     */
    public void kill() {
        dead = true;
    }

    /**
     * Tells whether the player has been killed.
     *
     * @return {@code true} if the player has been killed; otherwise,
     * {@code false}.
     */
    public boolean isDead() {
        return dead;
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        int offset;
        switch (color) {
            case GREEN:
                offset = 20;
                break;
            case RED:
                offset = 40;
                break;
            case BLUE:
                offset = 60;
                break;
            default:
                offset = 0;
                break;
        }

        if (isDead()) {
            offset += 12;
        } else {
            switch (latestDir) {
                case RIGHT:
                    offset += 3;
                    break;
                case LEFT:
                    offset += 6;
                    break;
                case UP:
                    offset += 9;
                    break;
            }
        }

        int offsetX = offset % 13;
        int offsetY = offset / 13;

        final int sourceX = offsetX * PLAYER_SIZE + offsetX;
        final int sourceY = offsetY * PLAYER_SIZE + offsetY;
        final int width = PLAYER_SIZE;
        final int height = PLAYER_SIZE;

        final int targetX = drawX + PLAYER_X_OFFSET * Grid.SCALE;
        final int targetY = drawY + PLAYER_Y_OFFSET * Grid.SCALE;
        final int targetWidth = width * Grid.SCALE;
        final int targetHeight = height * Grid.SCALE;

        g.drawImage(Players.IMAGE,
                targetX,
                targetY,
                targetX + targetWidth,
                targetY + targetHeight,
                sourceX,
                sourceY,
                sourceX + width,
                sourceY + height,
                observer);
    }

    public void update(Grid grid) {
        if (movementDirection == Direction.NONE || isDead()) {
            return;
        }

        int modX = drawX % Grid.TILE_SIZE;
        int modY = drawY % Grid.TILE_SIZE;

        alignAndMove(grid, getX(), getY(), modX, modY);
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
        int pos = horizontal ? drawX : drawY;
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
                drawX -= alignSpeed;
            } else {
                drawY -= alignSpeed;
            }
        } else {
            if (horizontal) {
                drawX += alignSpeed;
            } else {
                drawY += alignSpeed;
            }
        }
    }

    /**
     * Move the player without performing any checks.
     */
    private void moveInternal() {
        switch (movementDirection) {
            case UP:
                drawY -= speed;
                break;
            case DOWN:
                drawY += speed;
                break;
            case LEFT:
                drawX -= speed;
                break;
            case RIGHT:
                drawX += speed;
                break;
            default:
                break;
        }
    }
}
