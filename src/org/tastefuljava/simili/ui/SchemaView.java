package org.tastefuljava.simili.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.util.Properties;
import javax.swing.JComponent;
import org.tastefuljava.simili.render.RenderContext;
import org.tastefuljava.simili.model.Schema;

public class SchemaView extends JComponent {
    private final Properties props = new Properties();
    private Schema schema;

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

    @Override
    protected void paintComponent(Graphics g) {
        if (schema != null) {
            paintSchema((Graphics2D)g);
        }
    }

    private void paintSchema(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
        FontRenderContext frc = new FontRenderContext(null,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON,
                RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
        RenderContext pc = new RenderContext(frc, getProps());
        Rectangle rc = g.getClipBounds();
        pc.paint(g, schema, rc.x, rc.y, rc.width, rc.height);
    }
}
