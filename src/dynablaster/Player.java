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

        int tileX = getTileX();
        int tileY = getTileY();

        int modX = x % Grid.TILE_SIZE;
        int modY = y % Grid.TILE_SIZE;

        switch (movementDirection) {
            case UP:
                moveUp(grid, tileX, tileY, modX);
                break;
            case DOWN:
                moveDown(grid, tileX, tileY, modX);
                break;
            case LEFT:
                moveLeft(grid, tileX, tileY, modY);
                break;
            case RIGHT:
                moveRight(grid, tileX, tileY, modY);
                break;
        }

        if (y < 0) {
            y = 0;
        } else if (y > Grid.SIZE.height) {
            y = Grid.SIZE.height;
        }

        if (x < 0) {
            x = 0;
        } else if (x > Grid.SIZE.width) {
            x = Grid.SIZE.width;
        }
    }

    private void moveUp(Grid grid, int tileX, int tileY, int modX) {
        if (grid.canMoveTo(tileX, tileY - 1)) {
            // Move straight up
            align(true, false, true);
            return;
        }

        if (modX < Grid.TILE_SIZE / 2
                && modX != 0
                && grid.canMoveTo(tileX + 1, tileY - 1)) {
            // Move right and up
            align(true, true, true);
            return;
        }

        if (modX >= Grid.TILE_SIZE / 2
                && grid.canMoveTo(tileX - 1, tileY - 1)) {
            // Move left and up
            align(true, true, true);
            return;
        }
        
        // Only align vertically
        align(false, false, false);
    }

    private void moveDown(Grid grid, int tileX, int tileY, int modX) {
        if (grid.canMoveTo(tileX, tileY + 1)) {
            // Move straight down
            align(true, false, true);
            return;
        }

        if (modX < Grid.TILE_SIZE / 2
                && modX != 0
                && grid.canMoveTo(tileX + 1, tileY + 1)) {
            // Move right and down
            align(true, true, true);
            return;
        }

        if (modX >= Grid.TILE_SIZE / 2
                && grid.canMoveTo(tileX - 1, tileY + 1)) {
            // Move left and down
            align(true, true, true);
            return;
        }
        
        // Only align vertically
        align(false, false, false);
    }

    private void moveLeft(Grid grid, int tileX, int tileY, int mod) {
        if (grid.canMoveTo(tileX - 1, tileY)) {
            // Move straight left
            align(false, false, true);
            return;
        }

        if (mod < Grid.TILE_SIZE / 2
                && mod != 0
                && grid.canMoveTo(tileX - 1, tileY + 1)) {
            // Move up and left
            align(false, true, true);
            return;
        }

        if (mod >= Grid.TILE_SIZE / 2
                && grid.canMoveTo(tileX - 1, tileY - 1)) {
            // Move down and left
            align(false, true, true);
            return;
        }
        
        // Only align horizontally
        align(true, false, false);
    }

    private void moveRight(Grid grid, int tileX, int tileY, int mod) {
        if (grid.canMoveTo(tileX + 1, tileY)) {
            // Move straight right
            align(false, false, true);
            return;
        }

        if (mod < Grid.TILE_SIZE / 2
                && mod != 0
                && grid.canMoveTo(tileX + 1, tileY + 1)) {
            // Move up and right
            align(false, true, true);
            return;
        }

        if (mod >= Grid.TILE_SIZE / 2
                && grid.canMoveTo(tileX + 1, tileY - 1)) {
            // Move down and right
            align(false, true, true);
            return;
        }
        
        // Only align horizontally
        align(true, false, false);
    }

    private void align(boolean horizontal, boolean reverse, boolean moveIfAligned) {
        int pos = horizontal ? x : y;
        int mod = pos % Grid.TILE_SIZE;

        if (mod == 0) {
            if (moveIfAligned) {
                move();
            }
            return;
        }

        int alignSpeed = Math.min(mod, speed);

        if (!reverse && mod < Grid.TILE_SIZE / 2 || reverse && mod >= Grid.TILE_SIZE / 2) {
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

    private void move() {
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
