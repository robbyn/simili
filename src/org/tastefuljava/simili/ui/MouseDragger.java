package org.tastefuljava.simili.ui;

import java.awt.Graphics2D;

public interface MouseDragger {
    public void start(int x, int y);
    public void stop(int x, int y);
    public void drag(int x, int y);
    public void feedback(Graphics2D g);
}
