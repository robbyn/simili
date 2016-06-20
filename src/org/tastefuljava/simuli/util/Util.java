package org.tastefuljava.simuli.util;

import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    private static final String NUMBER_FORMAT = "0.####";
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

    public static DecimalFormat getDecimalFormat(String pattern) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat format = new DecimalFormat(pattern);
        format.setDecimalFormatSymbols(symbols);
        return format;
    }

    public static String dbl2str(double d, String pattern) {
        DecimalFormat format = getDecimalFormat(pattern);
        return format.format(d);
    }

    public static String dbl2str(double d) {
        return dbl2str(d, NUMBER_FORMAT);
    }

    public static double str2dbl(String s) {
        return str2dbl(s, NUMBER_FORMAT);
    }

    public static double str2dbl(String s, String pattern) {
        if (isBlank(s)) {
            return 0;
        } else {
            try {
                DecimalFormat format = getDecimalFormat(pattern);
                return format.parse(s).doubleValue();
            } catch (ParseException e) {
                throw new NumberFormatException("Invalid number " + s);
            }
        }
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }
}
