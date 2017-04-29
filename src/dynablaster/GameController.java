package dynablaster;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import javax.swing.JPanel;

public class GameController {

    private boolean gameOver = false;
    private MainFrame frame;

    public Grid grid;
    public Bombs bombs;
    public Players players;

    public GameController() {
        newGame();
    }

    public final void newGame() {
        grid = new Grid(this);
        bombs = new Bombs(this);
        players = new Players(this);
        
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
        frame.showVictoryScreen();
    }

    public void announceDraw() {
        gameOver = true;
        frame.showVictoryScreen();
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
