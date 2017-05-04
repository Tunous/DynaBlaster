package dynablaster;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

/**
 * Klasa reprezentująca gracza.
 */
public class Player {

    private static final int PLAYER_SIZE = 23;
    private static final int PLAYER_X_OFFSET = 13;
    private static final int PLAYER_Y_OFFSET = 9;

    /**
     * Współrzędna x w pikselach pozycji na której znajduje się gracz.
     */
    private int drawX;

    /**
     * Współrzędna y w pikselach pozycji na której znajduje się gracz.
     */
    private int drawY;

    /**
     * Ilość bomb jaką ten gracz może aktualnie postawić.
     */
    private int bombs = 1;

    /**
     * Maksymalny zasięg bomb postawionych przez tego gracza.
     */
    private int bombRange = 1;

    /**
     * Oznacza czy gracz zginął.
     */
    private boolean dead = false;

    /**
     * Kierunek w którym gracz aktualnie się porusza.
     */
    private Direction movementDirection = Direction.NONE;

    /**
     * Kierónek różny od {@code Direction.NONE} w którym ostatnio poruszał się
     * gracz.
     */
    private Direction latestDir = Direction.DOWN;

    /**
     * Czas kiedy gracz zaczął się poruszać.
     */
    private long movementStart = 0;

    /**
     * Czas kiedy gracz umarł.
     */
    private long deathStart = 0;

    /**
     * Oznacza czy animacja śmierci się zakończyła.
     */
    public boolean hasPlayedDeathAnimation = false;

    /**
     * Kolor tego gracza.
     */
    public final PlayerColor color;

    public Player(PlayerColor color, int spawnX, int spawnY) {
        this.color = color;

        drawX = spawnX * Grid.TILE_SIZE;
        drawY = spawnY * Grid.TILE_SIZE;
    }

    /**
     * Zwraca współrzędną x gracza .
     *
     * @return Współrzędna x.
     */
    public int getX() {
        return (drawX + Grid.TILE_SIZE / 2) / Grid.TILE_SIZE + 1;
    }

    /**
     * Zwraca współrzędną y gracza .
     *
     * @return Współrzędna y.
     */
    public int getY() {
        return (drawY + Grid.TILE_SIZE / 2) / Grid.TILE_SIZE + 1;
    }

    /**
     * Ustawia aktualny kierunek ruchu gracza. Jeśli podany kierunek jest różny
     * od {@code Direction.NONE} to gracz będzię się poruszał za każdym razem
     * gdzy zostanie wywołana funkcja {@code update()}.
     *
     * @param dir Kierunek w którym gracz ma się poruszać.
     */
    public void setMovementDirection(Direction dir) {
        if (dir == movementDirection) {
            return;
        }

        movementDirection = dir;
        if (dir != Direction.NONE) {
            latestDir = dir;
            movementStart = System.currentTimeMillis();
        }
    }

    /**
     * Zwiększa zasięg eksplozji bomb stawianych przez tego gracza o 1.
     */
    public void increaseRange() {
        bombRange += 1;
    }

    /**
     * Próbuje postawić bombę na podanych współrzędnych
     *
     * @param bombX Współrzędna x.
     * @param bombY Współrzędna y.
     * @return Postawiona bomba albo {@code null} jeśli nie postawiono żadnej
     * bomby.
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
     * Zwiększa ilość bomb jaką gracz może postawić o 1.
     */
    public void addBomb() {
        bombs += 1;
    }

    /**
     * Zwraca czy gracz może stawiać bomby..
     *
     * @return {@code true} jeśli gracz może stawiać bomby.
     */
    public boolean canPlaceBombs() {
        return bombs > 0;
    }

    /**
     * Zabija gracza.
     */
    public void kill() {
        if (isDead()) {
            return;
        }

        dead = true;
        deathStart = System.currentTimeMillis();
    }

    /**
     * Zwraca czy gracz został zabity.
     *
     * @return {@code true} jeśli gracz został zabity.
     */
    public boolean isDead() {
        return dead;
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        if (isDead() && hasPlayedDeathAnimation) {
            return;
        }

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

            long now = System.currentTimeMillis();
            long frame = (now - deathStart) / 100;
            if (frame >= 7) {
                hasPlayedDeathAnimation = true;
                frame = 7;
            }

            offset += frame;
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

            if (movementDirection != Direction.NONE) {
                // Animacja ruchu
                long now = System.currentTimeMillis();
                long timeOfMovement = (now - movementStart) / 150;
                long frame = timeOfMovement % 3;

                offset += frame;
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
     * Wyśrodkowuje gracza na pozycji i rozpoczyna ruch.
     *
     * @param grid Plansza gry.
     * @param tileX Współrzędna x.
     * @param tileY Współrzędna y.
     * @param modX Poziome przesunięcie gracza.
     * @param modY Pionowe przesunięcie gracza.
     */
    private void alignAndMove(Grid grid, int tileX, int tileY, int modX,
            int modY) {
        if (canMoveStraight(grid, tileX, tileY)) {
            // - Wyśrodkuj gracza w osi przeciwnej do osi ruchu
            // - Rozpocznij ruch po wyśrodkowaniu
            align(!movementDirection.isHorizontal(), false, true);
            return;
        }

        int mod = movementDirection.isHorizontal() ? modY : modX;

        final boolean canMoveToLeftSide = mod >= Grid.TILE_SIZE / 2
                && canMoveToSide(grid, tileX, tileY, Direction.LEFT);
        final boolean canMoveToRightSide = mod < Grid.TILE_SIZE / 2
                && mod != 0
                && canMoveToSide(grid, tileX, tileY, Direction.RIGHT);

        // Gracz nie może poruszyć się prosto ale może zostać przesunięty w bok
        // aby poruszyć się prosto.
        if (canMoveToLeftSide || canMoveToRightSide) {
            // - Przesuń gracza w bok
            // - Rozpocznij ruch po wyśrodkowaniu
            align(!movementDirection.isHorizontal(), true, true);
            return;
        }

        // Wyśrodkuj gracza w kierunku ruch co powoduje że poruszy się on tak
        // aby dotknąć ściany.
        align(movementDirection.isHorizontal(), false, false);
    }

    /**
     * Zwraca czy gracz może przejść przez pozycję znajdującą się na wprost od
     * niego.
     *
     * @param grid Plansza gry.
     * @param tileX Współrzędna x.
     * @param tileY Współrzędna y.
     * @return {@code true} jeśli gracz może się poruszyć.
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
     * Zwraca czy gracz może przejść przez pozycję znajdującą się obok elementu
     * na wprost od niego.
     *
     * @param grid Plansza gry.
     * @param tileX Współrzędna x.
     * @param tileY Współrzędna y.
     * @param side Jeśli {@code Direction.RIGHT} to funcja sprawdza pozycję w
     * kierunku zgodnym z kierunkiem zegara od elementu na wprost od gracza. W
     * przeciwnym wypadku sprawdza pozycję w kierunku przeciwnym do kierunku
     * ruchu zegara.
     * @return {@code true} jeślli gracz może się poruszyć.
     */
    private boolean canMoveToSide(Grid grid, int tileX, int tileY,
            Direction side) {

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
     * @param horizontal Jeśli {@code true} to gracz porusza się poziomo.
     * @param reverse Jeśli {@code true} to gracz powinien być wyśrodkowany tak
     * aby nie znajdował się na aktualnej pozycji.
     * @param moveIfAligned Oznacza czy gracz powinien zacząć się poruszać po
     * jego wyśrodkowaniu.
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

        int alignSpeed = Math.min(mod, Grid.SCALE);

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
     * Przesuń gracza bez dokonywania żadnych sprawdzeń.
     */
    private void moveInternal() {
        switch (movementDirection) {
            case UP:
                drawY -= Grid.SCALE;
                break;
            case DOWN:
                drawY += Grid.SCALE;
                break;
            case LEFT:
                drawX -= Grid.SCALE;
                break;
            case RIGHT:
                drawX += Grid.SCALE;
                break;
            default:
                break;
        }
    }
}
