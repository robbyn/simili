package org.tastefuljava.simuli.render;

import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PatchStyle {
    private static final Logger LOG
            = Logger.getLogger(PatchStyle.class.getName());
    private static final Map<String,Color> COLOR_MAP = buildColorMap();

    private final Font titleFont;
    private final Font pinNameFont;
    private final int pinWidth;
    private final int borderWidth;
    private final int gutterWidth;
    private final Color background;
    private final Color foreground;

    public PatchStyle(Properties props) {
        titleFont = getFont(props, "patch-title-font", "Arial-plain-20");
        pinNameFont = getFont(props, "pin-name-font", "Arial-italic-16");
        pinWidth = getInt(props, "pin-width", 12);
        borderWidth = getInt(props, "patch-border-width", 2);
        gutterWidth = getInt(props, "patch-gutter-width", 3);
        background = getColor(props, "patch-background", "lightGray");
        foreground = getColor(props, "patch-foreground", "black");
    }

    public Font getTitleFont() {
        return titleFont;
    }

    public Font getPinNameFont() {
        return pinNameFont;
    }

    public int getPinWidth() {
        return pinWidth;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public int getGutterWidth() {
        return gutterWidth;
    }

    public Color getBackground() {
        return background;
    }

    public Color getForeground() {
        return foreground;
    }

    protected static int getInt(Properties props, String key, int def) {
        String s = props.getProperty(key);
        return s == null ? def : Integer.parseInt(s);
    }

    protected static Font getFont(Properties props, String key, String def) {
        return Font.decode(props.getProperty(key, def));
    }

    protected static Color getColor(Properties props, String key, String def) {
        String s = props.getProperty(key, def);
        Color result = COLOR_MAP.get(s);
        if (result == null) {
            result = Color.decode(props.getProperty(key, def));
        }
        return result;
    }

    private static Map<String, Color> buildColorMap() {
        Map<String, Color> result = new HashMap<>();
        try {
            for (Field var: Color.class.getFields()) {
                if (Modifier.isStatic(var.getModifiers())
                        && var.getType() == Color.class) {
                    result.put(var.getName(), (Color) var.get(null));
                }
            }
        } catch (IllegalAccessException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
