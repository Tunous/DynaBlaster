package dynablaster;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import javax.swing.JPanel;

public class GameController {

    private boolean gameOver = false;
    private MainFrame frame;

    public final Grid grid;
    public final Bombs bombs;
    public final Players players;

    public GameController() {
        grid = new Grid(this);
        bombs = new Bombs(this);
        players = new Players(this);

        newGame();
    }

    public final void newGame() {
        grid.newGame();
        bombs.newGame();
        players.resetPlayers();

        gameOver = false;
    }

    public void setFrame(MainFrame frame) {
        this.frame = frame;
    }

    public void registerKeyListener(JPanel panel) {
        panel.addKeyListener(KeyListenerWrapper.init(players, false));
    }

    public void announceWinner(Player player) {
        gameOver = true;
        frame.showVictoryScreen(player);
    }

    public void announceDraw() {
        gameOver = true;
        frame.showVictoryScreen(null);
    }

    public void update() {
        if (gameOver) {
            return;
        }

        players.update();
        bombs.update();
    }

    public void draw(Graphics2D g, ImageObserver observer) {
        if (gameOver) {
            return;
        }

        grid.draw(g, observer);
        bombs.draw(g, observer);
        players.draw(g, observer);
    }
}
