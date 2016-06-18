package org.tastefuljava.simili.geometry;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import org.tastefuljava.simili.model.Input;
import org.tastefuljava.simili.model.Output;
import org.tastefuljava.simili.model.Patch;
import org.tastefuljava.simili.model.Pin;

public class PaintContext {
    private final FontRenderContext frc;
    private final Properties props;
    private Font patchTitleFont;
    private Font pinNameFont;
    private int pinWidth = -1;
    private int patchBorderWidth = -1;
    private int patchSeparatorWidth = -1;
    private final Map<Patch, PatchMetrics> patchMetricsCache = new WeakHashMap<>();

    public PaintContext(FontRenderContext frc, Properties props) {
        this.frc = frc;
        this.props = props;
    }

    public Font getPatchTitleFont() {
        return patchTitleFont = requireFont(patchTitleFont, "patch-title-font",
                "Helvetica-plain-10");
    }

    public Font getPinNameFont() {
        return pinNameFont = requireFont(pinNameFont, "pin-name-font",
                "Helvetica-italic-8");
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
                "patch-border-width", 1);
    }

    public Dimension patchTitleSize(String title) {
        return getTextSize(title, getPatchTitleFont());
    }

    public Dimension pinNameSize(String name) {
        return getTextSize(name, getPinNameFont());
    }

    public Dimension patchSize(Patch patch) {
        PatchMetrics pm = patchMetrics(patch);
        return pm.getSize();
    }

    public Rectangle inputBounds(Input in) {
        Patch patch = in.getPatch();
        PatchMetrics pm = patchMetrics(patch);
        Rectangle bounds = pm.getInputPinBounds(in.getIndex());
        bounds.translate(patch.getX(), patch.getY());
        return bounds;
    }

    public Rectangle outputBounds(Output out) {
        Patch patch = out.getPatch();
        PatchMetrics pm = patchMetrics(patch);
        Rectangle bounds = pm.getOutputPinBounds(out.getIndex());
        bounds.translate(patch.getX(), patch.getY());
        return bounds;
    }

    public Rectangle connectionBounds(Input in) {
        Rectangle rc = inputBounds(in);
        if (in.isConnected()) {
            rc.add(outputBounds(in.getSource()));
        }
        return rc;
    }

    private PatchMetrics patchMetrics(Patch patch) {
        PatchMetrics metrics = patchMetricsCache.get(patch);
        if (metrics == null) {
            Dimension titleSize = patchTitleSize(patch.getTitle());
            List<Dimension> inputSize = pinColumnSize(patch.getInputs());
            List<Dimension> outputSize = pinColumnSize(patch.getOutputs());
            metrics = new PatchMetrics(titleSize, inputSize, outputSize);
            patchMetricsCache.put(patch, metrics);
        }
        return metrics;
    }

    private <T extends Pin> Rectangle pinBoundsInColumn(Iterable<T> pins,
            T pin, int y, int x) {
        int width = 0;
        int height = 0;
        boolean found = false;
        for (Pin p : pins) {
            Dimension dim = pinNameSize(p.getName());
            width = Math.max(width, dim.width + getPinWidth());
            if (!found) {
                found = pin == p;
                if (found) {
                    height = dim.height;
                } else {
                    y += dim.height;
                }
            }
        }
        return new Rectangle(x, y, width, height);
    }

    private <T extends Pin> List<Dimension> pinColumnSize(Iterable<T> pins) {
        List<Dimension> result = new ArrayList<>();
        for (Pin pin : pins) {
            Dimension dim = pinNameSize(pin.getName());
            dim.width += getPinWidth();
            result.add(dim);
        }
        return result;
    }

    private Dimension getTextSize(String title, Font font) {
        TextLayout layout = new TextLayout(title, font, frc);
        Rectangle2D rc = layout.getBounds();
        return new Dimension((int) Math.ceil(rc.getWidth()),
                (int) Math.ceil(rc.getHeight()));
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


    private static int columnWidth(Collection<Dimension> dims) {
        int result = 0;
        for (Dimension dim : dims) {
            int w = dim.width;
            if (w > result) {
                result = w;
            }
        }
        return result;
    }

    private static int[] columnHeight(Collection<Dimension> dims) {
        int[] result = new int[dims.size()];
        int i = 0;
        for (Dimension dim : dims) {
            result[i++] = dim.height;
        }
        return result;
    }

    private static int sum(int[] array) {
        int sum = 0;
        for (int e : array) {
            sum += e;
        }
        return sum;
    }

    private class PatchMetrics {
        private final int width;
        private final int height;
        private final int titleWidth;
        private final int titleHeight;
        private final int inputWidth;
        private final int[] inputHeight;
        private final int outputWidth;
        private final int[] outputHeight;

        PatchMetrics(Dimension titleSize, Collection<Dimension> inputSize,
                Collection<Dimension> outputSize) {
            int bw = getPatchBorderWidth();
            int sw = getPatchSeparatorWidth();
            int pw = getPinWidth();
            inputWidth = columnWidth(inputSize) + pw;
            inputHeight = columnHeight(inputSize);
            outputWidth = columnWidth(outputSize) + pw;
            outputHeight = columnHeight(outputSize);
            titleWidth = Math.max(titleSize.width,
                    inputWidth + sw + outputWidth);
            titleHeight = titleSize.height;
            width = 2 * bw + titleWidth;
            height = 2 * bw
                    + titleHeight
                    + sw
                    + Math.max(sum(inputHeight), sum(outputHeight));
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public Dimension getSize() {
            return new Dimension(width, height);
        }

        public int getTitleWidth() {
            return titleWidth;
        }

        public int getTitleHeight() {
            return titleHeight;
        }

        public Dimension getTitleSize() {
            return new Dimension(titleWidth, titleHeight);
        }

        public Dimension getInputSize(int i) {
            return new Dimension(inputWidth, inputHeight[i]);
        }

        public Dimension getOutputSize(int i) {
            return new Dimension(outputWidth, outputHeight[i]);
        }

        public Rectangle getTitleBounds() {
            int bw = getPatchBorderWidth();
            return new Rectangle(bw, bw, titleWidth, titleHeight);
        }

        public Rectangle getInputPinBounds(int i) {
            int bw = getPatchBorderWidth();
            int sw = getPatchSeparatorWidth();
            int pw = getPinWidth();
            int x = bw;
            int y = bw + titleHeight + sw;
            for (int k = 0; k < i; ++k) {
                y += inputHeight[k];
            }
            return new Rectangle(x, y, pw, inputHeight[i]);
        }

        public Rectangle getOutputPinBounds(int i) {
            int bw = getPatchBorderWidth();
            int sw = getPatchSeparatorWidth();
            int pw = getPinWidth();
            int x = width - bw - pw;
            int y = bw + titleHeight + sw;
            for (int k = 0; k < i; ++k) {
                y += outputHeight[k];
            }
            return new Rectangle(x, y, pw, outputHeight[i]);
        }
    }
}
