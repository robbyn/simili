package org.tastefuljava.simili.ui;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import static java.lang.Boolean.TRUE;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.tastefuljava.simili.model.Input;
import org.tastefuljava.simili.model.Output;
import org.tastefuljava.simili.model.Patch;
import org.tastefuljava.simili.render.RenderContext;
import org.tastefuljava.simili.model.Schema;
import org.tastefuljava.simili.render.HitTester;

public class SchemaView extends JComponent
        implements MouseListener, MouseMotionListener {
    private static final Logger LOG
            = Logger.getLogger(SchemaView.class.getName());
    private static final Object TEXT_ANTIALIAS
            = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
    private static final Object FRACTIONALMETRICS
            = RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT;

    private final Properties props = new Properties();
    private Schema schema;

    public SchemaView() {
        initialize();
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
        RenderContext pc = getRenderContext();
        Rectangle rc = g.getClipBounds();
        pc.paint(g, schema, rc.x, rc.y, rc.width, rc.height);
    }

    private RenderContext getRenderContext() {
        FontRenderContext frc = new FontRenderContext(
                null, TEXT_ANTIALIAS, FRACTIONALMETRICS);
        return new RenderContext(frc, getProps());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (schema != null) {
            RenderContext pc = getRenderContext();
            pc.hitTest(schema, e.getX(), e.getY(), new HitTester<Boolean>() {
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

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
