package org.tastefuljava.simuli.ui.dragger;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import org.tastefuljava.simuli.model.Patch;
import org.tastefuljava.simuli.render.RenderContext;
import org.tastefuljava.simuli.ui.MouseDragger;
import org.tastefuljava.simuli.ui.SchemaView;

public class PatchDragger implements MouseDragger {
    private final Patch patch;
    private final Point pos;
    private final Dimension size;
    private int dx;
    private int dy;
    private final SchemaView view;

    public PatchDragger(Patch patch, SchemaView view) {
        this.view = view;
        this.patch = patch;
        RenderContext rc = RenderContext.current();
        size = rc.patchSize(patch);
        pos = view.schema2component(patch.getX(), patch.getY());
    }

    @Override
    public void start(int x, int y) {
        dx = pos.x - x;
        dy = pos.y - y;
        view.updateSize();
    }

    @Override
    public void stop(int x, int y) {
        moveTo(x, y);
        Point pt = view.component2schema(pos.x, pos.y);
        patch.setPosition(pt.x, pt.y);
        view.updateSize();
    }

    @Override
    public void drag(int x, int y) {
        moveTo(x, y);
        view.updateSize();
    }

    @Override
    public void feedback(Graphics2D g) {
        g.drawRect(pos.x, pos.y, size.width, size.height);
    }

    private void moveTo(int x, int y) {
        pos.x = x + dx;
        pos.y = y + dy;
    }    
}
