package org.tastefuljava.simuli.render;

import java.awt.Graphics2D;
import java.awt.Point;
import org.tastefuljava.simuli.model.Patch;

public interface PatchView {
    public int getWidth();
    public int getHeight();
    public Point getInputPinPosition(int i);
    public Point getOutputPinPosition(int i);
    public <T> T hitTest(Patch patch, int xt, int yt, HitTester<T> tester);
    public void paint(Graphics2D g, Patch patch, int x, int y);
}
