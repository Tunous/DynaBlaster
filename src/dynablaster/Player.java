package dynablaster;

public class Player {

    private int x;
    private int y;
    private int speed = 1;
    private Direction movementDirection = Direction.NONE;

    public Player(int spawnX, int spawnY) {
        x = spawnX * 16;
        y = spawnY * 16;
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
        speed += 1;
    }

    public void move() {
        int xTile = x / 16;
        int yTile = y / 16;

        switch (movementDirection) {
            case UP:
            case DOWN:
                if (x % 16 != 0) {
                    if (xTile % 2 == 0) {
                        x -= 1;
                    } else {
                        x += 1;
                    }
                } else if (xTile % 2 == 0) {
                    y += movementDirection == Direction.UP ? -speed : speed;
                }
                break;
            case LEFT:
            case RIGHT:
                if (y % 16 != 0) {
                    if (yTile % 2 == 0) {
                        y -= 1;
                    } else {
                        y += 1;
                    }
                } else if (yTile % 2 == 0) {
                    x += movementDirection == Direction.LEFT ? -speed : speed;
                }
                break;
                
            default:
                return;
        }

        if (y < 0) {
            y = 0;
        } else if (y > 160) {
            y = 160;
        }

        if (x < 0) {
            x = 0;
        } else if (x > 160) {
            x = 160;
        }
    }
}
