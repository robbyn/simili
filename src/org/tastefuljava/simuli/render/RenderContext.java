package org.tastefuljava.simuli.render;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.CubicCurve2D;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tastefuljava.simuli.model.Input;
import org.tastefuljava.simuli.model.Output;
import org.tastefuljava.simuli.model.Patch;
import org.tastefuljava.simuli.model.Pin;
import org.tastefuljava.simuli.model.Schema;

public class RenderContext implements Closeable {
    private static final Logger LOG
            = Logger.getLogger(RenderContext.class.getName());
    private static final ThreadLocal<RenderContext> CURRENT
            = new ThreadLocal<>();

    private final FontRenderContext frc;
    private final Properties props;
    private Font patchTitleFont;
    private Font pinNameFont;
    private int pinWidth = -1;
    private int patchBorderWidth = -1;
    private int patchSeparatorWidth = -1;
    private final Map<Patch, PatchView> patchViews = new HashMap<>();

    public static RenderContext open(Properties props, Object aaHint,
            Object fmHint) {
        if (CURRENT.get() != null) {
            throw new IllegalStateException("A RenderContext is already open");
        }
        RenderContext rc = new RenderContext(props, aaHint, fmHint);
        CURRENT.set(rc);
        return rc;
    }

    public static RenderContext current() {
        RenderContext rc = CURRENT.get();
        if (rc == null) {
            throw new IllegalStateException("No current RenderContext");
        }
        return rc;
    }

    private RenderContext(Properties props, Object aaHint, Object fmHint) {
        this.frc = new FontRenderContext(null, aaHint, fmHint);
        this.props = props;
    }

    @Override
    public void close() {
        if (CURRENT.get() != this) {
            throw new IllegalStateException("Not the current RenderContext");
        }
        CURRENT.set(null);
    }

    public Font getPatchTitleFont() {
        return patchTitleFont = requireFont(patchTitleFont, "patch-title-font",
                "Helvetica-plain-20");
    }

    public Font getPinNameFont() {
        return pinNameFont = requireFont(pinNameFont, "pin-name-font",
                "Helvetica-italic-16");
    }

    public int getPinWidth() {
        return pinWidth = requireInt(pinWidth, "pin-width", 12);
    }

    public int getPatchBorderWidth() {
        return patchBorderWidth = requireInt(patchBorderWidth,
                "patch-border-width", 2);
    }

    public int getPatchSeparatorWidth() {
        return patchSeparatorWidth = requireInt(patchSeparatorWidth,
                "patch-border-width", 3);
    }

    public Dimension patchTitleSize(String title) {
        return stringSize(title, getPatchTitleFont());
    }

    public Dimension pinNameSize(String name) {
        return stringSize(name, getPinNameFont());
    }

    public Dimension patchSize(Patch patch) {
        PatchView pm = patchView(patch);
        return new Dimension(pm.getWidth(), pm.getHeight());
    }

    public Point inputPosition(Input in) {
        Patch patch = in.getPatch();
        PatchView pm = patchView(patch);
        Point pos = pm.getInputPinPosition(in.getIndex());
        pos.translate(patch.getX(), patch.getY());
        return pos;
    }

    public Point outputPosition(Output out) {
        Patch patch = out.getPatch();
        PatchView pm = patchView(patch);
        Point pos = pm.getOutputPinPosition(out.getIndex());
        pos.translate(patch.getX(), patch.getY());
        return pos;
    }

    public Rectangle connectionBounds(Input in) {
        Rectangle rc = new Rectangle(inputPosition(in));
        if (in.isConnected()) {
            rc.add(outputPosition(in.getSource()));
        }
        int pw = getPinWidth();
        int halfPw = (pw+1)/2;
        rc.x -= halfPw;
        rc.y -= halfPw;
        rc.width += pw;
        rc.height += pw;
        return rc;
    }

    public Iterable<Patch> filterPatches(Iterable<Patch> patches, int x, int y,
            int w, int h) {
        int right = x + w;
        int bottom = y + h;
        List<Patch> result = new ArrayList<>();
        for (Patch patch: patches) {
            int px = patch.getX();
            int py = patch.getY();
            if (px < right && py < bottom) {
                Dimension dim = patchSize(patch);
                int pr = px + dim.width;
                int pb = py + dim.height;
                if (pr > x && pb > y) {
                    result.add(patch);
                }
            }
        }
        return result;
    }

    public Iterable<Input> filterConnections(Iterable<Patch> patches,
            int x, int y, int w, int h) {
        Rectangle visible = new Rectangle(x, y, w, h);
        List<Input> result = new ArrayList<>();
        for (Patch patch: patches) {
            for (Input in: patch.getInputs()) {
                if (in.isConnected()) {
                    Rectangle rc = connectionBounds(in);
                    if (rc.intersects(visible)) {
                        result.add(in);
                    }
                }
            }
        }
        return result;
    }

    public void paint(Graphics2D g, Schema schema, int x, int y,
            int w, int h, int xt, int yt) {
        Point pt = schema.getLeftTop();
        int xs = xt-pt.x ;
        int ys = yt-pt.y;
        paintConnections(g, filterConnections(schema.patches(), x, y, w, h),
                xs, ys);
        paintPatches(g, filterPatches(schema.patches(), x, y, w, h), xs, ys);
    }

    public <T> T hitTest(Schema schema, int x, int y, HitTester<T> tester) {
        for (Patch patch: schema.descending()) {
            if (x >= patch.getX() && y >= patch.getY()) {
                PatchView pm = patchView(patch);
                T result = pm.hitTest(x, y, tester);
                if (result != null) {
                    return result;
                }
            }
        }
        return tester.background();
    }

    public Rectangle getBounds(Schema schema) {
        Rectangle rc = new Rectangle();
        for (Patch patch: schema.patches()) {
            PatchView pm = patchView(patch);
            rc.add(new Rectangle(patch.getX(), patch.getY(),
                    pm.getWidth(), pm.getHeight()));
        }
        return rc;
    }

    public void paintConnection(Graphics2D g, int x1, int y1, int x2, int y2) {
        if (y1 == y2 || x1 == x2) {
            g.drawLine(x1, y1, x2, y2);
        } else {
            double xc = (x1+x2)/2.0;
            Shape s = new CubicCurve2D.Double(x1, y1, xc, y1, xc, y2, x2, y2);
            g.draw(s);
        }
    }

    public void paintPin(Graphics2D g, Pin pin, int x, int y, int w, int h) {
        if (pin.isConnected()) {
            g.fillOval(x, y, w, h);
        } else {
            g.drawOval(x, y, w, h);
        }
    }

    public Dimension stringSize(String s, Font font) {
        TextLayout layout = new TextLayout(s, font, frc);
        return textSize(layout);
    }

    public void paintString(Graphics2D g, String s, Font font, int x, int y,
            int w, int h, HorizontalAlignment ha, VerticalAlignment va) {
        Rectangle bounds = new Rectangle(x, y, w, h);
        TextLayout layout = new TextLayout(s, font, frc);
        Dimension size = textSize(layout);
        ha.adjustWidth(bounds, size.width);
        va.adjustHeight(bounds, size.height);
        layout.draw(g, bounds.x, bounds.y+layout.getAscent());
    }

    private Dimension textSize(TextLayout layout) {
        return new Dimension((int)Math.ceil(layout.getAdvance()),
                (int)Math.ceil(layout.getAscent() + layout.getDescent()));
    }

    private PatchView patchView(Patch patch) {
        PatchView view = patchViews.get(patch);
        if (view == null) {
            view = new DefaultPatchView(this, patch);
            patchViews.put(patch, view);
        }
        return view;
    }

    public int columnSize(Iterable<? extends Pin> pins, int[] height) {
        int pw = getPinWidth();
        int sw = getPatchBorderWidth();
        int width = 0;
        int i = 0;
        for (Pin pin: pins) {
            Dimension size = pinNameSize(pin.getName());
            width = Math.max(width, size.width + sw + pw);
            height[i++] = Math.max(pw, size.height);
        }
        return width;
    }

    private Font requireFont(Font font, String key, String def) {
        return font != null ? font : Font.decode(props.getProperty(key, def));
    }

    private int requireInt(int value, String key, int def) {
        if (value >= 0) {
            return value;
        }
        String s = props.getProperty(key);
        return s == null ? def : Integer.parseInt(s);
    }


    private void paintConnections(Graphics2D g, Iterable<Input> inputs,
            int x, int y) {
        for (Input in: inputs) {
            paintConnection(g, in, x, y);
        }
    }

    private void paintConnection(Graphics2D g, Input in, int x, int y) {
        Point ip = inputPosition(in);
        Point op = outputPosition(in.getSource());
        paintConnection(g, x + ip.x, y + ip.y, x + op.x, y + op.y);
    }

    private void paintPatches(Graphics2D g, Iterable<Patch> patches,
            int x, int y) {
        for (Patch patch: patches) {
            paintPatch(g, patch, x + patch.getX(), y + patch.getY());
        }
    }

    private void paintPatch(Graphics2D g, Patch patch, int x, int y) {
        PatchView pm = patchView(patch);
        pm.paint(g, x, y);
    }
}
