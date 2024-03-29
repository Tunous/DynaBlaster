package dynablaster;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.util.HashMap;
import java.util.HashSet;

public class Players extends KeyAdapter {

    public static Image IMAGE;

    private final HashMap<PlayerColor, Integer> latestKeyPresses = new HashMap<>();
    private final HashMap<PlayerColor, Player> players = new HashMap<>();
    private final HashSet<PlayerColor> enabledPlayers = new HashSet<>();

    private final GameController controller;

    public Players(GameController controller) {
        this.controller = controller;

        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        IMAGE = toolkit.getImage("res/players.png");

        enabledPlayers.add(PlayerColor.WHITE);
        enabledPlayers.add(PlayerColor.GREEN);
        resetPlayers();
    }

    public final void resetPlayers() {
        players.clear();

        if (enabledPlayers.contains(PlayerColor.WHITE)) {
            players.put(PlayerColor.WHITE, new Player(PlayerColor.WHITE, 0, 0));
        }

        if (enabledPlayers.contains(PlayerColor.GREEN)) {
            players.put(PlayerColor.GREEN, new Player(PlayerColor.GREEN, 10, 10));
        }
        if (enabledPlayers.contains(PlayerColor.RED)) {
            players.put(PlayerColor.RED, new Player(PlayerColor.RED, 10, 0));
        }
        if (enabledPlayers.contains(PlayerColor.BLUE)) {
            players.put(PlayerColor.BLUE, new Player(PlayerColor.BLUE, 0, 10));
        }
    }

    public void setPlayerEnabled(PlayerColor playerColor, boolean selected) {
        if (selected) {
            enabledPlayers.add(playerColor);
        } else {
            enabledPlayers.remove(playerColor);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            // WHITE
            case KeyEvent.VK_UP:
                setMovementDirection(PlayerColor.WHITE, Direction.UP, e.getKeyCode());
                break;
            case KeyEvent.VK_DOWN:
                setMovementDirection(PlayerColor.WHITE, Direction.DOWN, e.getKeyCode());
                break;
            case KeyEvent.VK_LEFT:
                setMovementDirection(PlayerColor.WHITE, Direction.LEFT, e.getKeyCode());
                break;
            case KeyEvent.VK_RIGHT:
                setMovementDirection(PlayerColor.WHITE, Direction.RIGHT, e.getKeyCode());
                break;
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_CONTROL:
                placeBomb(PlayerColor.WHITE);
                break;

            // GREEN
            case KeyEvent.VK_W:
                setMovementDirection(PlayerColor.GREEN, Direction.UP, e.getKeyCode());
                break;
            case KeyEvent.VK_S:
                setMovementDirection(PlayerColor.GREEN, Direction.DOWN, e.getKeyCode());
                break;
            case KeyEvent.VK_A:
                setMovementDirection(PlayerColor.GREEN, Direction.LEFT, e.getKeyCode());
                break;
            case KeyEvent.VK_D:
                setMovementDirection(PlayerColor.GREEN, Direction.RIGHT, e.getKeyCode());
                break;
            case KeyEvent.VK_Q:
            case KeyEvent.VK_E:
                placeBomb(PlayerColor.GREEN);
                break;

            // RED
            case KeyEvent.VK_I:
                setMovementDirection(PlayerColor.RED, Direction.UP, e.getKeyCode());
                break;
            case KeyEvent.VK_K:
                setMovementDirection(PlayerColor.RED, Direction.DOWN, e.getKeyCode());
                break;
            case KeyEvent.VK_J:
                setMovementDirection(PlayerColor.RED, Direction.LEFT, e.getKeyCode());
                break;
            case KeyEvent.VK_L:
                setMovementDirection(PlayerColor.RED, Direction.RIGHT, e.getKeyCode());
                break;
            case KeyEvent.VK_U:
            case KeyEvent.VK_O:
                placeBomb(PlayerColor.RED);
                break;

            // BLUE
            case KeyEvent.VK_NUMPAD8:
                setMovementDirection(PlayerColor.BLUE, Direction.UP, e.getKeyCode());
                break;
            case KeyEvent.VK_NUMPAD2:
                setMovementDirection(PlayerColor.BLUE, Direction.DOWN, e.getKeyCode());
                break;
            case KeyEvent.VK_NUMPAD4:
                setMovementDirection(PlayerColor.BLUE, Direction.LEFT, e.getKeyCode());
                break;
            case KeyEvent.VK_NUMPAD6:
                setMovementDirection(PlayerColor.BLUE, Direction.RIGHT, e.getKeyCode());
                break;
            case KeyEvent.VK_NUMPAD5:
            case KeyEvent.VK_NUMPAD0:
                placeBomb(PlayerColor.BLUE);
                break;

            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            // WHITE
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                setMovementDirection(PlayerColor.WHITE, Direction.NONE, e.getKeyCode());
                break;

            // GREEN
            case KeyEvent.VK_W:
            case KeyEvent.VK_S:
            case KeyEvent.VK_A:
            case KeyEvent.VK_D:
                setMovementDirection(PlayerColor.GREEN, Direction.NONE, e.getKeyCode());
                break;

            // RED
            case KeyEvent.VK_I:
            case KeyEvent.VK_K:
            case KeyEvent.VK_J:
            case KeyEvent.VK_L:
                setMovementDirection(PlayerColor.RED, Direction.NONE, e.getKeyCode());
                break;

            // BLUE
            case KeyEvent.VK_NUMPAD8:
            case KeyEvent.VK_NUMPAD2:
            case KeyEvent.VK_NUMPAD4:
            case KeyEvent.VK_NUMPAD6:
                setMovementDirection(PlayerColor.BLUE, Direction.NONE, e.getKeyCode());
                break;

            default:
                break;
        }
    }

    /**
     * Zabija wszystkich graczy znajdujących się na podanej pozycji.
     *
     * @param x Współrzędna x.
     * @param y Współrzędna y.
     */
    public void killAt(int x, int y) {
        for (Player player : players.values()) {
            if (player.getX() == x && player.getY() == y) {
                player.kill();
            }
        }
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        for (Player player : players.values()) {
            player.draw(g, observer);
        }
    }

    public void update() {
        for (Player player : players.values()) {
            if (controller.bombs.hasEnteredExplosion(player)) {
                player.kill();
            }
            player.update(controller.grid);
            controller.grid.collectPowerup(player);
        }

        checkWinner();
    }

    private void setMovementDirection(PlayerColor color, Direction direction,
            int keyCode) {
        Player player = players.get(color);
        if (player == null) {
            return;
        }

        if (direction == Direction.NONE) {
            if (latestKeyPresses.getOrDefault(color, 0) == keyCode) {
                player.setMovementDirection(direction);
            }
        } else {
            player.setMovementDirection(direction);
            latestKeyPresses.put(color, keyCode);
        }
    }

    private void placeBomb(PlayerColor color) {
        Player player = players.get(color);
        if (player == null || player.isDead()) {
            return;
        }

        int x = player.getX();
        int y = player.getY();

        controller.bombs.placeBomb(player, x, y);
    }

    /**
     * Sprawdza czy ktoś wygrał.
     */
    private void checkWinner() {
        Player lastAlivePlayer = null;
        int alivePlayersCount = 0;

        for (Player player : players.values()) {
            if (!player.isDead()) {
                alivePlayersCount += 1;
                if (alivePlayersCount > 1) {
                    return;
                }
                lastAlivePlayer = player;
            } else if (!player.hasPlayedDeathAnimation) {
                return;
            }
        }

        controller.announceWinner(lastAlivePlayer);
    }
}
