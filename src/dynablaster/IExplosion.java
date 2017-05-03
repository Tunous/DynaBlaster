/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynablaster;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;

/**
 *
 * @author luke
 */
public interface IExplosion {
    void draw(Graphics2D g, ImageObserver observer, Image image);
    boolean hasTimedOut();
    boolean isInRange(int x, int y);
}
