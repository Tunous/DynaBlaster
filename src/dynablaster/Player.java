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
    
    public interface GridChecker {
        boolean canMoveTo(int tileX, int tileY);
    }

    public void move(GridChecker checker) {
        int tileX = (x + 8) / 16 + 1;
        int tileY = (y + 8) / 16 + 1;

        switch (movementDirection) {
            case UP:
                if (checker.canMoveTo(tileX, tileY - 1)) {
                    if (align(true)) {
                        y -= speed;
                    }
                } else {
                    align(false);
                }
                break;
            case DOWN:
                if (checker.canMoveTo(tileX, tileY + 1)) {
                    if (align(true)) {
                        y += speed;
                    }
                } else {
                    align(false);
                }
                break;
            case LEFT:
                if (checker.canMoveTo(tileX - 1, tileY)) {
                    if (align(false)) {
                        x -= speed;
                    }
                } else {
                    align(true);
                }
                break;
            case RIGHT:
                if (checker.canMoveTo(tileX + 1, tileY)) {
                    if (align(false)) {
                        x += speed;
                    }
                } else {
                    align(true);
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
    
    private boolean align(boolean horizontal) {
        int pos = horizontal ? x : y;
        int mod = pos % 16;
        
        if (mod == 0) {
            return true;
        }
        
        if (mod < 8) {
            if (horizontal) {
                x -= 1;
            } else {
                y -= 1;
            }
        } else {
            if (horizontal) {
                x += 1;
            } else {
                y += 1;
            }
        }
        
        return false;
    }
}
