package org.tastefuljava.simili.util;

import java.awt.Rectangle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    private static final Pattern RECTANGLE_RE = Pattern.compile(
            "([+-]?[0-9]+),([+-]?[0-9]+),([0-9]+),([0-9]+)");

    public static Rectangle parseRect(String s) {
        Matcher matcher = RECTANGLE_RE.matcher(s);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid rectangle [" + s + "]");
        }
        return new Rectangle(
                Integer.parseInt(matcher.group(1)),
                Integer.parseInt(matcher.group(2)),
                Integer.parseInt(matcher.group(3)),
                Integer.parseInt(matcher.group(4)));
    }

    public static String formatRect(Rectangle rc) {
        return Integer.toString(rc.x) + ","
                + Integer.toString(rc.y) + ","
                + Integer.toString(rc.width) + ","
                + Integer.toString(rc.height);
    }
}
