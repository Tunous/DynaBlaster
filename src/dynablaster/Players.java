package dynablaster;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;

public class Players extends KeyAdapter {

    private final Image[] playerImages;
    private final Player[] players;
    private final int[] latestKeyPresses;
    
    private final GameController controller;

    public Players(GameController controller) {
        this.controller = controller;
        
        players = new Player[2];
        players[0] = new Player(PlayerColor.WHITE, 0, 0);
        players[1] = new Player(PlayerColor.GREEN, 10, 10);

        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        playerImages = new Image[players.length];
        for (int i = 0; i < playerImages.length; i++) {
            playerImages[i] = toolkit.getImage(players[i].color.fileName);
        }
        
        latestKeyPresses = new int[players.length];
    }
    
    public void newGame() {
        players[0] = new Player(PlayerColor.WHITE, 0, 0);
        players[1] = new Player(PlayerColor.GREEN, 10, 10);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                getPlayer(PlayerColor.WHITE).setMovementDirection(Direction.UP);
                latestKeyPresses[0] = e.getKeyCode();
                break;
            case KeyEvent.VK_DOWN:
                getPlayer(PlayerColor.WHITE).setMovementDirection(Direction.DOWN);
                latestKeyPresses[0] = e.getKeyCode();
                break;
            case KeyEvent.VK_LEFT:
                getPlayer(PlayerColor.WHITE).setMovementDirection(Direction.LEFT);
                latestKeyPresses[0] = e.getKeyCode();
                break;
            case KeyEvent.VK_RIGHT:
                getPlayer(PlayerColor.WHITE).setMovementDirection(Direction.RIGHT);
                latestKeyPresses[0] = e.getKeyCode();
                break;
            case KeyEvent.VK_SPACE:
                Player player = getPlayer(PlayerColor.WHITE);
                int x = player.getTileX();
                int y = player.getTileY();

                controller.bombs.placeBomb(player, x, y);
                break;
                
            case KeyEvent.VK_W:
                getPlayer(PlayerColor.GREEN).setMovementDirection(Direction.UP);
                latestKeyPresses[1] = e.getKeyCode();
                break;
            case KeyEvent.VK_S:
                getPlayer(PlayerColor.GREEN).setMovementDirection(Direction.DOWN);
                latestKeyPresses[1] = e.getKeyCode();
                break;
            case KeyEvent.VK_A:
                getPlayer(PlayerColor.GREEN).setMovementDirection(Direction.LEFT);
                latestKeyPresses[1] = e.getKeyCode();
                break;
            case KeyEvent.VK_D:
                getPlayer(PlayerColor.GREEN).setMovementDirection(Direction.RIGHT);
                latestKeyPresses[1] = e.getKeyCode();
                break;
            case KeyEvent.VK_Q:
                Player greenPlayer = getPlayer(PlayerColor.GREEN);
                int gX = greenPlayer.getTileX();
                int gY = greenPlayer.getTileY();

                controller.bombs.placeBomb(greenPlayer, gX, gY);
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
                if (latestKeyPresses[0] == e.getKeyCode()) {
                    getPlayer(PlayerColor.WHITE).setMovementDirection(Direction.NONE);
                }
                break;
            case KeyEvent.VK_W:
            case KeyEvent.VK_S:
            case KeyEvent.VK_A:
            case KeyEvent.VK_D:
                if (latestKeyPresses[1] == e.getKeyCode()) {
                    getPlayer(PlayerColor.GREEN).setMovementDirection(Direction.NONE);
                }
                break;
            default:
                break;
        }
    }

    public void update() {
        for (Player player : players) {
            player.update(controller.grid);
        }
        
        checkWinner();
    }

    private void checkWinner() {
        Player lastAlivePlayer = null;
        int alivePlayersCount = 0;
        
        for (Player player : players) {
            if (!player.isDead()) {
                alivePlayersCount += 1;
                if (alivePlayersCount > 1) {
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

    public void draw(Graphics2D g, ImageObserver observer) {
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            if (player.isDead()) {
                continue;
            }

            g.drawImage(playerImages[i],
                    player.getX() + 13,
                    player.getY() + 9,
                    observer);
        }
    }

    public void killAt(int x, int y) {
        for (Player player : players) {
            if (player.getTileX() == x && player.getTileY() == y) {
                player.kill();
            }
        }
    }

    public Player getPlayer(PlayerColor color) {
        switch (color) {
            case WHITE:
                return players[0];
            case GREEN:
                return players[1];
            default:
                return null;
        }
    }
}
