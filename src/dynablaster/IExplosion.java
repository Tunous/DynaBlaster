package dynablaster;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;

public interface IExplosion {
    void draw(Graphics2D g, ImageObserver observer, Image image);
    boolean hasTimedOut();
    boolean isInRange(int x, int y);
}
