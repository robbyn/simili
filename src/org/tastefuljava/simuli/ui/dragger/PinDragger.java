package org.tastefuljava.simuli.ui.dragger;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tastefuljava.simuli.model.Pin;
import org.tastefuljava.simuli.render.RenderContext;
import org.tastefuljava.simuli.ui.MouseDragger;
import org.tastefuljava.simuli.ui.SchemaView;

abstract class PinDragger<T extends Pin> implements MouseDragger {
    private static final Logger LOG
            = Logger.getLogger(PinDragger.class.getName());

    protected final SchemaView view;
    protected final T pin;
    protected final Point pinPos;
    protected int x;
    protected int y;

    protected PinDragger(SchemaView view, T pin, Point pos) {
        this.view = view;
        this.pin = pin;
        pinPos = view.schema2component(pos);
    }

    protected abstract void connect();

    @Override
    public void start(int x, int y) {
        moveTo(x, y);
        view.repaint();
    }

    @Override
    public void stop(int x, int y) {
        moveTo(x, y);
        connect();
        view.repaint();
    }

    @Override
    public void drag(int x, int y) {
        LOG.log(Level.INFO, "drag {0},{1}", new Object[]{x, y});
        moveTo(x, y);
        view.repaint();
    }

    @Override
    public void feedback(Graphics2D g) {
        LOG.log(Level.INFO, "paintConnection {0},{1},{2},{3}",
                new Object[]{pinPos.x, pinPos.y, x, y});
        RenderContext rc = RenderContext.current();
        rc.paintConnection(g, pinPos.x, pinPos.y, x, y);
    }

    private void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
