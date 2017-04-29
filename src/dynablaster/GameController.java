package dynablaster;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import javax.swing.JPanel;

public class GameController {
    
    private MainFrame gameFrame;

    public final Grid grid;
    public final Bombs bombs;
    public final Players players;

    public GameController() {
        grid = new Grid(this);
        bombs = new Bombs(this);
        players = new Players(this);
    }
    
    public void setFrame(MainFrame frame) {
        gameFrame = frame;
    }

    public void registerKeyListener(JPanel panel) {
        panel.addKeyListener(KeyListenerWrapper.init(players, false));
    }
    
    public void announceWinner(Player player) {
        gameFrame.showWinScreen();
    }
    
    public void announceDraw() {
        gameFrame.showWinScreen();
    }

    public void update() {
        players.update();
        bombs.update();
    }
    
    public void draw(Graphics2D g, ImageObserver observer) {
        grid.draw(g, observer);
        bombs.draw(g, observer);
        players.draw(g, observer);
    }
}
