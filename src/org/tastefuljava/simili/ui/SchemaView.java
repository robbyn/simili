package org.tastefuljava.simili.ui;

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
import org.tastefuljava.simili.model.Input;
import org.tastefuljava.simili.model.Output;
import org.tastefuljava.simili.model.Patch;
import org.tastefuljava.simili.render.RenderContext;
import org.tastefuljava.simili.model.Schema;
import org.tastefuljava.simili.render.Grabbers;
import org.tastefuljava.simili.render.HitTester;

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
                    public Boolean patchTitle(Patch patch) {
                        LOG.log(Level.INFO, "patchTitle [{0}]", patch.getTitle());
                        return TRUE;
                    }

                    @Override
                    public Boolean patch(Patch patch) {
                        LOG.log(Level.INFO, "patch [{0}]", patch.getTitle());
                        return TRUE;
                    }

                    @Override
                    public Boolean inputPin(Patch patch, Input in) {
                        LOG.log(Level.INFO, "inputPin [{0}] - [{1}]",
                                new Object[]{patch.getTitle(), in.getName()});
                        return TRUE;
                    }

                    @Override
                    public Boolean inputName(Patch patch, Input in) {
                        LOG.log(Level.INFO, "inputName [{0}] - [{1}]",
                                new Object[]{patch.getTitle(), in.getName()});
                        return TRUE;
                    }

                    @Override
                    public Boolean outputPin(Patch patch, Output out) {
                        LOG.log(Level.INFO, "outputPin [{0}] - [{1}]",
                                new Object[]{patch.getTitle(), out.getName()});
                        return TRUE;
                    }

                    @Override
                    public Boolean outputName(Patch patch, Output out) {
                        LOG.log(Level.INFO, "outputName [{0}] - [{1}]",
                                new Object[]{patch.getTitle(), out.getName()});
                        return TRUE;
                    }

                    @Override
                    public Boolean background() {
                        LOG.info("background");
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
        if (e.getButton() == MouseEvent.BUTTON1) {
            e.consume();
            if (dragger != null) {
                try (RenderContext rc = this.openRenderContext()) {
                    dragger.drag(e.getX(), e.getY());
                }
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
            public MouseDragger patchTitle(Patch patch) {
                LOG.warning("patchTitle dragger not supported yet");
                return null;
            }

            @Override
            public MouseDragger patch(Patch patch) {
                return new PatchDragger(patch);
            }

            @Override
            public MouseDragger inputPin(Patch patch, Input in) {
                return new InputDragger(in);
            }

            @Override
            public MouseDragger inputName(Patch patch, Input in) {
                LOG.warning("inputName dragger not supported yet");
                return null;
            }

            @Override
            public MouseDragger outputPin(Patch patch, Output out) {
                return new OutputDragger(out);
            }

            @Override
            public MouseDragger outputName(Patch patch, Output out) {
                LOG.warning("outputName dragger not supported yet");
                return null;
            }

            @Override
            public MouseDragger background() {
                return null;
            }
        });
    }

    private <T> T hitTest(int x, int y, HitTester<T> tester) {
        if (schema == null) {
            return null;
        }
        Point pt = component2schema(x, y);
        RenderContext pc = RenderContext.current();
        return pc.hitTest(schema, pt.x, pt.y, tester);
    }

    private Point component2schema(int x, int y) {
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

    private Point component2schema(Point pt) {
        return component2schema(pt.x, pt.y);
    }

    private Point schema2component(int x, int y) {
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

    private Point schema2component(Point pt) {
        return schema2component(pt.x, pt.y);
    }

    private class InputDragger implements MouseDragger {
        private final Input input;
        private final Point pinPos;
        private int x;
        private int y;

        public InputDragger(Input in) {
            this.input = in;
            RenderContext rc = RenderContext.current();
            pinPos = schema2component(rc.inputPosition(in));
        }

        @Override
        public void start(int x, int y) {
            drag(x, y);
        }

        @Override
        public void stop(int x, int y) {
            drag(x, y);
            Output out = hitTest(x, y, Grabbers.OUTPUT_GRABBER);
            if (out != null) {
                input.setSource(out);
            }
        }

        @Override
        public void drag(int x, int y) {
            this.x = x;
            this.y = y;
            repaint();
        }

        @Override
        public void feedback(Graphics2D g) {
            RenderContext rc = RenderContext.current();
            rc.paintConnection(g, pinPos.x, pinPos.y, x, y);
        }
    }

    private class OutputDragger implements MouseDragger {
        private final Output output;
        private final Point pinPos;
        private int x;
        private int y;

        public OutputDragger(Output out) {
            this.output = out;
            RenderContext rc = RenderContext.current();
            pinPos = schema2component(rc.outputPosition(out));
        }

        @Override
        public void start(int x, int y) {
            drag(x, y);
        }

        @Override
        public void stop(int x, int y) {
            drag(x, y);
            Input in = hitTest(x, y, Grabbers.INPUT_GRABBER);
            if (in != null) {
                in.setSource(output);
            }
        }

        @Override
        public void drag(int x, int y) {
            this.x = x;
            this.y = y;
            repaint();
        }

        @Override
        public void feedback(Graphics2D g) {
            RenderContext rc = RenderContext.current();
            rc.paintConnection(g, pinPos.x, pinPos.y, x, y);
        }
    }

    private class PatchDragger implements MouseDragger {
        private final Patch patch;
        private Point pos;
        private Dimension size;
        private int dx;
        private int dy;

        public PatchDragger(Patch patch) {
            this.patch = patch;
            RenderContext rc = RenderContext.current();
            size = rc.patchSize(patch);
            pos = schema2component(patch.getX(), patch.getY());
        }

        @Override
        public void start(int x, int y) {
            dx = pos.x - x;
            dy = pos.y - y;
            repaint();
        }

        @Override
        public void stop(int x, int y) {
            drag(x, y);
            Point pt = component2schema(pos.x, pos.y);
            patch.setPosition(pt.x, pt.y);
        }

        @Override
        public void drag(int x, int y) {
            pos.x = x + dx;
            pos.y = y + dy;
            repaint();
        }

        @Override
        public void feedback(Graphics2D g) {
            g.drawRect(pos.x, pos.y, size.width, size.height);
        }
    }
}
