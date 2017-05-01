package dynablaster;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.util.HashMap;

public class Players extends KeyAdapter {

    private final HashMap<PlayerColor, Integer> latestKeyPresses = new HashMap<>();
    private final HashMap<PlayerColor, Image> playerImages = new HashMap<>();
    private final HashMap<PlayerColor, Player> players = new HashMap<>();

    private final GameController controller;

    public Players(GameController controller) {
        this.controller = controller;

        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        for (PlayerColor color : PlayerColor.values()) {
            Image image = toolkit.getImage(color.fileName);
            playerImages.put(color, image);
        }

        resetPlayers();
    }

    public final void resetPlayers() {
        players.clear();
        players.put(PlayerColor.WHITE, new Player(PlayerColor.WHITE, 0, 0));
        players.put(PlayerColor.GREEN, new Player(PlayerColor.GREEN, 10, 10));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
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
                placeBomb(PlayerColor.WHITE);
                break;

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
                placeBomb(PlayerColor.GREEN);
                break;

            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                setMovementDirection(PlayerColor.WHITE, Direction.NONE, e.getKeyCode());
                break;

            case KeyEvent.VK_W:
            case KeyEvent.VK_S:
            case KeyEvent.VK_A:
            case KeyEvent.VK_D:
                setMovementDirection(PlayerColor.GREEN, Direction.NONE, e.getKeyCode());
                break;

            default:
                break;
        }
    }

    private void setMovementDirection(PlayerColor color, Direction direction,
            int keyCode) {
        Player player = players.get(color);

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
        int x = player.getTileX();
        int y = player.getTileY();

        controller.bombs.placeBomb(player, x, y);
    }

    /**
     * Kill all players that are located at the tile with specified coordinates.
     *
     * @param x The horizontal position, in tiles.
     * @param y The vertical position, in tiles.
     */
    public void killAt(int x, int y) {
        for (Player player : players.values()) {
            if (player.getTileX() == x && player.getTileY() == y) {
                player.kill();
            }
        }
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        for (Player player : players.values()) {
            final Image image = playerImages.get(player.color);
            player.draw(g, observer, image);
        }
    }

    public void update() {
        for (Player player : players.values()) {
            player.update(controller.grid);
            controller.grid.collectPowerup(player);
        }

        checkWinner();
    }

    /**
     * Check if someone has won this round.
     */
    private void checkWinner() {
        Player lastAlivePlayer = null;
        int alivePlayersCount = 0;

        for (Player player : players.values()) {
            if (!player.isDead()) {
                alivePlayersCount += 1;
                if (alivePlayersCount > 1) {
                    // More than 1 alive player means that the game is still
                    // running. Do not do anything.
                    return;
                }
                lastAlivePlayer = player;
            }
        }

        if (alivePlayersCount == 1) {
            controller.announceWinner(lastAlivePlayer);
        } else {
            controller.announceDraw();
        }
    }
}
