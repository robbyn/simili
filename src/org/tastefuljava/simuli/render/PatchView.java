package org.tastefuljava.simuli.render;

import java.awt.Graphics2D;
import java.awt.Point;

public interface PatchView {
    public int getX();
    public int getY();
    public int getWidth();
    public int getHeight();
    public Point getInputPinPosition(int i);
    public Point getOutputPinPosition(int i);
    public void paint(Graphics2D g, int x, int y);
    public <T> T hitTest(int xt, int yt, HitTester<T> tester);
}
