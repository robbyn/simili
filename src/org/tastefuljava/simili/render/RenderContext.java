package org.tastefuljava.simili.render;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
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
import org.tastefuljava.simili.model.Schema;

public class RenderContext {
    private final FontRenderContext frc;
    private final Properties props;
    private Font patchTitleFont;
    private Font pinNameFont;
    private int pinWidth = -1;
    private int patchBorderWidth = -1;
    private int patchSeparatorWidth = -1;
    private final Map<Patch, PatchMetrics> patchMetricsCache = new WeakHashMap<>();

    public RenderContext(FontRenderContext frc, Properties props) {
        this.frc = frc;
        this.props = props;
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

    public Point inputPosition(Input in) {
        Patch patch = in.getPatch();
        PatchMetrics pm = patchMetrics(patch);
        Point pos = pm.getInputPinPosition(in.getIndex());
        pos.translate(patch.getX(), patch.getY());
        return pos;
    }

    public Point outputPosition(Output out) {
        Patch patch = out.getPatch();
        PatchMetrics pm = patchMetrics(patch);
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

    public Iterable<Patch> filterPatches(Schema schema, int x, int y,
            int w, int h) {
        int right = x + w;
        int bottom = y + h;
        List<Patch> result = new ArrayList<>();
        for (Patch patch: schema) {
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

    public Iterable<Input> filterConnections(Schema schema, int x, int y,
            int w, int h) {
        Rectangle visible = new Rectangle(x, y, w, h);
        List<Input> result = new ArrayList<>();
        for (Patch patch: schema) {
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
            int w, int h) {
        paintConnections(g, filterConnections(schema, x, y, w, h), x, y);
        paintPatches(g, filterPatches(schema, x, y, w, h), x, y);
    }

    public <T> T hitTest(Schema schema, int x, int y, HitTester<T> tester) {
        for (Patch patch: schema) {
            int px = patch.getX();
            int py = patch.getY();
            if (px < x && py < y) {
                PatchMetrics pm = patchMetrics(patch);
                T result = pm.hitTest(patch, x - px, y - py, tester);
                if (result != null) {
                    return result;
                }
            }
        }
        return tester.background();
    }

    public Rectangle getBounds(Schema schema) {
        Rectangle rc = new Rectangle();
        for (Patch patch: schema) {
            PatchMetrics pm = patchMetrics(patch);
            rc.add(new Rectangle(patch.getX(), patch.getY(),
                    pm.getWidth(), pm.getHeight()));
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

    private void paintConnections(Graphics2D g, Iterable<Input> inputs,
            int x, int y) {
        for (Input in: inputs) {
            paintConnection(g, in, x, y);
        }
    }

    private void paintConnection(Graphics2D g, Input in, int x, int y) {
        Point ip = inputPosition(in);
        Point op = outputPosition(in.getSource());
        g.drawLine(x + ip.x, y + ip.y, x + op.x, y + op.y);
    }

    private void paintPatches(Graphics2D g, Iterable<Patch> patches,
            int x, int y) {
        for (Patch patch: patches) {
            paintPatch(g, patch, x + patch.getX(), y + patch.getY());
        }
    }

    private void paintPatch(Graphics2D g, Patch patch, int x, int y) {
        PatchMetrics pm = patchMetrics(patch);
        Dimension size = pm.getSize();
        g.drawRect(x, y, size.width, size.height);
        Rectangle rc = pm.getTitleBounds();
        rc.translate(x, y);
        TextLayout layout = new TextLayout(patch.getTitle(),
                getPatchTitleFont(), frc);
        layout.draw(g, rc.x, rc.y + layout.getAscent());
        int pw = getPinWidth();
        int sw = getPatchSeparatorWidth();
        int i = 0;
        for (Input in: patch.getInputs()) {
            rc = pm.getInputBounds(i++);
            rc.translate(x, y);
            layout = new TextLayout(in.getName(), getPinNameFont(), frc);
            layout.draw(g, rc.x + pw + sw, rc.y + layout.getAscent());
            drawPin(g, in, rc.x, rc.y + (rc.height-pw)/2, pw, pw);
        }
        i = 0;
        for (Output out: patch.getOutputs()) {
            rc = pm.getOutputBounds(i++);
            rc.translate(x, y);
            layout = new TextLayout(out.getName(), getPinNameFont(), frc);
            Rectangle2D tbounds = layout.getBounds();
            int tx = rc.x + rc.width - pw - sw
                    - (int)Math.ceil(tbounds.getWidth());
            layout.draw(g, tx, rc.y + layout.getAscent());
            drawPin(g, out, rc.x + rc.width - pw, rc.y + (rc.height-pw)/2,
                    pw, pw);
        }
    }

    private void drawPin(Graphics2D g, Pin pin, int x, int y, int w, int h) {
        if (pin.isConnected()) {
            g.fillOval(x, y, w, h);
        } else {
            g.drawOval(x, y, w, h);
        }
    }

    private <T> T hitTest(Patch patch, PatchMetrics pm, int x, int y,
            HitTester<T> tester) {
        if (x > pm.getWidth() || y > pm.getHeight()) {
            return null;
        }
        Rectangle rc = pm.getTitleBounds();
        if (rc.contains(x, y)) {
            return tester.patchTitle(patch);
        }
        int bw = getPatchBorderWidth();
        int sw = getPatchSeparatorWidth();
        int pw = getPinWidth();
        if (y > rc.y + rc.height + sw
                && x >= rc.x && x < rc.x + rc.width) {
            if (x < rc.x + pm.getInputWidth()) {
                int i = 0;
                for (Input in: patch.getInputs()) {
                    rc = pm.getInputBounds(i++);
                    if (rc.contains(x, y)) {
                        if (x < rc.x+pw) {
                            return tester.inputPin(patch, in);
                        }
                    }
                }
            } else if (x >= rc.x + rc.width - pm.getOutputWidth()) {
                
            }
        }
        return tester.patch(patch);
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
            inputWidth = columnWidth(inputSize) + pw + sw;
            inputHeight = columnHeight(inputSize);
            outputWidth = columnWidth(outputSize) + pw + sw;
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

        public int getInputWidth() {
            return inputWidth;
        }

        public Dimension getInputSize(int i) {
            return new Dimension(inputWidth, inputHeight[i]);
        }

        public int getOutputWidth() {
            return outputWidth;
        }

        public Dimension getOutputSize(int i) {
            return new Dimension(outputWidth, outputHeight[i]);
        }

        public Rectangle getTitleBounds() {
            int bw = getPatchBorderWidth();
            return new Rectangle(bw, bw, titleWidth, titleHeight);
        }

        public Point getInputPinPosition(int i) {
            Rectangle rc = getInputBounds(i);
            int pw = getPinWidth();
            return new Point(rc.x + pw/2, rc.y + rc.height/2);
        }

        public Point getOutputPinPosition(int i) {
            Rectangle rc = getOutputBounds(i);
            int pw = getPinWidth();
            return new Point(rc.x + rc.width - (pw+1)/2, rc.y + rc.height/2);
        }

        public Rectangle getInputBounds(int i) {
            int bw = getPatchBorderWidth();
            int sw = getPatchSeparatorWidth();
            int pw = getPinWidth();
            int x = bw;
            int y = bw + titleHeight + sw;
            for (int k = 0; k < i; ++k) {
                y += inputHeight[k];
            }
            return new Rectangle(x, y, inputWidth, inputHeight[i]);
        }

        public Rectangle getOutputBounds(int i) {
            int bw = getPatchBorderWidth();
            int sw = getPatchSeparatorWidth();
            int pw = getPinWidth();
            int x = bw + inputWidth + sw;
            int y = bw + titleHeight + sw;
            for (int k = 0; k < i; ++k) {
                y += outputHeight[k];
            }
            return new Rectangle(x, y, outputWidth, outputHeight[i]);
        }

        public <T> T hitTest(Patch patch, int x, int y, HitTester<T> tester) {
            int bw = getPatchBorderWidth();
            int sw = getPatchSeparatorWidth();
            int pw = getPinWidth();
            x -= bw;
            y -= bw;
            int w = width - 2*bw;
            int h = height - 2*bw;
            if (x < 0 || y < 0 || x >= w || y >= h) {
                return null;
            }
            y -= titleHeight;
            if (y < 0) {
                return tester.patchTitle(patch);
            }
            y -= sw;
            if (y >= 0) {
                if (x < inputWidth) {
                    int i = 0;
                    for (Input in: patch.getInputs()) {
                        y -= inputHeight[i++];
                        if (y < 0) {
                            if (x < pw) {
                                return tester.inputPin(patch, in);
                            } else if (x >= pw + sw) {
                                return tester.inputName(patch, in);
                            } else {
                                break;
                            }
                        }
                        y -= sw;
                        if (y < 0) {
                            break;
                        }
                    }
                }
                x -= inputWidth - sw;
                if (x >= 0 && x < outputWidth) {
                    int i = 0;
                    for (Output out: patch.getOutputs()) {
                        y -= outputHeight[i++];
                        if (y < 0) {
                            if (x >= outputWidth - pw) {
                                return tester.outputPin(patch, out);
                            } else if (x < outputWidth - pw - sw) {
                                return tester.outputName(patch, out);
                            } else {
                                break;
                            }
                        }
                        y -= sw;
                        if (y < 0) {
                            break;
                        }
                    }
                }
            }
            return tester.patch(patch);
        }
    }
}
