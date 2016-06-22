package org.tastefuljava.simuli.ui;

import org.tastefuljava.simuli.ui.dragger.PatchDragger;
import org.tastefuljava.simuli.ui.dragger.InputDragger;
import org.tastefuljava.simuli.ui.dragger.OutputDragger;
import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import static java.lang.Boolean.TRUE;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import org.tastefuljava.simuli.model.Input;
import org.tastefuljava.simuli.model.Output;
import org.tastefuljava.simuli.model.Patch;
import org.tastefuljava.simuli.render.RenderContext;
import org.tastefuljava.simuli.model.Schema;
import org.tastefuljava.simuli.render.HitTester;
import org.tastefuljava.simuli.util.ListenerList;

public class SchemaView extends JComponent
        implements Scrollable, MouseListener, MouseMotionListener {
    private static final Logger LOG
            = Logger.getLogger(SchemaView.class.getName());
    private static final Object TEXT_ANTIALIAS
            = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
    private static final Object FRACTIONALMETRICS
            = RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT;

    private final Properties props = new Properties();
    private Schema schema;
    private Insets margin = new Insets(10, 10, 10, 10);
    private MouseDragger dragger;
    private Patch selection;

    private final ListenerList listeners = new ListenerList();
    private final SelectionListener selectionNotifier
            = listeners.getNotifier(SelectionListener.class);

    public SchemaView() {
        initialize();
    }

    public Insets getMargin() {
        return margin;
    }

    public void setMargin(Insets newValue) {
        if (newValue != null) {
            margin = newValue;
            updateSize();
        }
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
        repaint();
    }

    public Properties getProps() {
        Properties result = new Properties();
        result.putAll(props);
        return result;
    }

    public void setProps(Properties newProps) {
        props.putAll(newProps);
        repaint();
    }

    public <T> T hitTest(int x, int y, HitTester<T> tester) {
        if (schema == null) {
            return null;
        }
        Point pt = component2schema(x, y);
        RenderContext pc = RenderContext.current();
        return pc.hitTest(schema, pt.x, pt.y, tester);
    }

    public Point component2schema(int x, int y) {
        Point pt;
        if (schema == null) {
            pt = new Point(x, y);
        } else {
            pt = schema.getLeftTop();
            pt.translate(x, y);
        }
        pt.translate(-margin.left, -margin.top);
        return pt;
    }

    public Point schema2component(int x, int y) {
        Point pt;
        if (schema == null) {
            pt = new Point(x, y);
        } else {
            pt = schema.getLeftTop();
            pt.x = x - pt.x;
            pt.y = y - pt.y;
        }
        pt.translate(margin.left, margin.top);
        return pt;
    }

    public Point schema2component(Point pt) {
        return schema2component(pt.x, pt.y);
    }

    public void addSelectionListener(SelectionListener listener) {
        listeners.addListener(listener);
    }

    public void removeSelectionListener(SelectionListener listener) {
        listeners.removeListener(listener);
    }

    private void initialize() {
        addMouseListener(this);
        addMouseMotionListener(this);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK
                | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    public void updateSize() {
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (schema != null) {
            paintSchema((Graphics2D)g);
        }
    }

    private void paintSchema(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                TEXT_ANTIALIAS);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                FRACTIONALMETRICS);
        try (RenderContext pc = openRenderContext()) {
            Rectangle rc = g.getClipBounds();
            pc.paint(g, schema, rc.x, rc.y, rc.width, rc.height,
                    margin.left, margin.top);
            if (dragger != null) {
                dragger.feedback(g);
            }
        }
    }

    private RenderContext openRenderContext() {
        return RenderContext.open(getProps(),
                TEXT_ANTIALIAS, FRACTIONALMETRICS);
    }

    @Override
    public Dimension getPreferredSize() {
        if (schema == null) {
            return super.getPreferredSize();
        } else {
            try (RenderContext pc = openRenderContext()) {
                Rectangle rc = pc.getBounds(schema);
                int width = rc.width + margin.left + margin.right;
                int height = rc.height + margin.top + margin.bottom;
                return new Dimension(Math.max(width, getWidth()),
                        Math.max(height, getHeight()));
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (schema != null) {
            try (final RenderContext pc = openRenderContext()) {
                hitTest(e.getX(), e.getY(),
                        new HitTester<Boolean>() {
                    @Override
                    public Boolean patch(Patch patch) {
                        selection = patch;
                        selectionNotifier.selectionChanged(
                                new Patch[] {selection});
                        return TRUE;
                    }

                    @Override
                    public Boolean background() {
                        return TRUE;
                    }
                });
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            e.consume();
            try (RenderContext rc = this.openRenderContext()) {
                dragger = dragger(e.getX(), e.getY());
                if (dragger != null) {
                    LOG.log(Level.INFO, "start {0},{1}",
                            new Object[]{e.getX(), e.getY()});
                    dragger.start(e.getX(), e.getY());
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            e.consume();
            if (dragger != null) {
                try (RenderContext rc = this.openRenderContext()) {
                    dragger.stop(e.getX(), e.getY());
                }
                dragger = null;
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragger != null) {
            e.consume();
            LOG.log(Level.INFO, "mouseDragged {0},{1}",
                    new Object[]{e.getX(), e.getY()});
            try (RenderContext rc = this.openRenderContext()) {
                dragger.drag(e.getX(), e.getY());
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    /* Scrollable */

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return visibleRect.width/2;
        } else {
            return visibleRect.height/2;
        }
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 1;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    private MouseDragger dragger(int x, int y) {
        if (schema == null) {
            return null;
        }
        return hitTest(x, y, new HitTester<MouseDragger>() {
            @Override
            public MouseDragger patch(Patch patch) {
                return new PatchDragger(patch, SchemaView.this);
            }

            @Override
            public MouseDragger inputPin(Patch patch, Input in) {
                return new InputDragger(SchemaView.this, in);
            }

            @Override
            public MouseDragger outputPin(Patch patch, Output out) {
                return new OutputDragger(SchemaView.this, out);
            }
        });
    }

}
