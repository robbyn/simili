package org.tastefuljava.simuli.render;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import org.tastefuljava.simuli.model.Input;
import org.tastefuljava.simuli.model.Output;
import org.tastefuljava.simuli.model.Patch;

class PatchView {
    private final int width;
    private final int height;
    private final int titleWidth;
    private final int titleHeight;
    private final int inputWidth;
    private final int[] inputHeight;
    private final int outputWidth;
    private final int[] outputHeight;
    private final RenderContext rc;

    PatchView(Patch patch, RenderContext rc) {
        this.rc = rc;
        int bw = rc.getPatchBorderWidth();
        int sw = rc.getPatchSeparatorWidth();
        int pw = rc.getPinWidth();
        Dimension titleSize = rc.stringSize(patch.getTitle(),
                rc.getPatchTitleFont());
        inputHeight = new int[patch.getInputCount()];
        inputWidth = rc.columnSize(patch.getInputs(), inputHeight);
        outputHeight = new int[patch.getOutputCount()];
        outputWidth = rc.columnSize(patch.getOutputs(), outputHeight);
        titleWidth = Math.max(titleSize.width, inputWidth + sw + outputWidth);
        titleHeight = titleSize.height;
        width = 2 * bw + titleWidth;
        height = 2 * bw + titleHeight + sw
                + Math.max(sum(inputHeight), sum(outputHeight));
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    Dimension getSize() {
        return new Dimension(width, height);
    }

    Point getInputPinPosition(int i) {
        Rectangle bounds = getInputBounds(i);
        int pw = rc.getPinWidth();
        return new Point(bounds.x + pw / 2, bounds.y + bounds.height / 2);
    }

    Point getOutputPinPosition(int i) {
        Rectangle bounds = getOutputBounds(i);
        int pw = rc.getPinWidth();
        return new Point(bounds.x + bounds.width - (pw + 1) / 2,
                bounds.y + bounds.height / 2);
    }

    private Rectangle getInputBounds(int i) {
        int bw = rc.getPatchBorderWidth();
        int sw = rc.getPatchSeparatorWidth();
        int pw = rc.getPinWidth();
        int x = bw;
        int y = bw + titleHeight + sw;
        for (int k = 0; k < i; ++k) {
            y += inputHeight[k];
        }
        return new Rectangle(x, y, inputWidth, inputHeight[i]);
    }

    private Rectangle getOutputBounds(int i) {
        int bw = rc.getPatchBorderWidth();
        int sw = rc.getPatchSeparatorWidth();
        int pw = rc.getPinWidth();
        int x = bw + inputWidth + sw;
        int y = bw + titleHeight + sw;
        for (int k = 0; k < i; ++k) {
            y += outputHeight[k];
        }
        return new Rectangle(x, y, outputWidth, outputHeight[i]);
    }

    public void paint(Graphics2D g, Patch patch, int x, int y) {
        int bw = rc.getPatchBorderWidth();
        int sw = rc.getPatchSeparatorWidth();
        paintBorder(g, x, y);
        x += bw;
        y += bw;
        paintTitle(g, patch, x, y);
        y += titleHeight + sw;
        paintInputRow(g, patch.getInputs(), x, y);
        x += titleWidth - outputWidth;
        paintOutputRow(g, patch.getOutputs(), x, y);
    }

    private void paintOutput(Graphics2D g, Output out, int x, int y,
            int w, int h) {
        int pw = rc.getPinWidth();
        int sw = rc.getPatchSeparatorWidth();
        rc.paintString(g, out.getName(), rc.getPinNameFont(), x, y,
                w-pw-sw, h, HorizontalAlignment.RIGHT, VerticalAlignment.TOP);
        rc.paintPin(g, out, x + w - pw, y + (h - pw) / 2, pw, pw);
    }

    private void paintInput(Graphics2D g, Input in, int x, int y,
            int w, int h) {
        int pw = rc.getPinWidth();
        int sw = rc.getPatchSeparatorWidth();
        rc.paintPin(g, in, x, y + (h - pw) / 2, pw, pw);
        rc.paintString(g, in.getName(), rc.getPinNameFont(), x + pw + sw, y,
                w - pw - sw, h,
                HorizontalAlignment.LEFT, VerticalAlignment.TOP);
    }

    private void paintTitle(Graphics2D g, Patch patch, int x, int y) {
        rc.paintString(g, patch.getTitle(), rc.getPatchTitleFont(), x, y,
                titleWidth, titleHeight,
                HorizontalAlignment.LEFT, VerticalAlignment.TOP);
    }

    private void paintBorder(Graphics2D g, int x, int y) {
        g.drawRect(x, y, width, height);
    }

    public <T> T hitTest(Patch patch, int xt, int yt, HitTester<T> tester) {
        int bw = rc.getPatchBorderWidth();
        int sw = rc.getPatchSeparatorWidth();
        int pw = rc.getPinWidth();
        int x = patch.getX();
        int y = patch.getY();
        int r = x + width;
        int b = y + height;
        if (xt < x || yt < y || xt >= r || yt >= b) {
            return null;
        }
        x += bw;
        y += bw;
        r -= bw;
        b -= bw;
        if (xt < x || yt < y || xt >= r || yt >= b) {
            // we're in the border
            return tester.patch(patch);
        }
        y += titleHeight;
        if (yt < y) {
            return tester.patchTitle(patch);
        }
        y += sw;
        if (yt < y) {
            return tester.patch(patch);
        }
        if (xt < x + inputWidth) {
            int i = 0;
            for (Input in : patch.getInputs()) {
                y += inputHeight[i++];
                if (yt < y) {
                    if (xt < x + pw) {
                        return tester.inputPin(patch, in);
                    } else if (xt >= x + pw + sw) {
                        return tester.inputName(patch, in);
                    } else {
                        break;
                    }
                }
            }
            return tester.patch(patch);
        } else if (xt >= r - outputWidth) {
            int i = 0;
            for (Output out : patch.getOutputs()) {
                y += outputHeight[i++];
                if (yt < y) {
                    if (xt < r - pw - sw) {
                        return tester.outputName(patch, out);
                    } else if (xt >= r - pw) {
                        return tester.outputPin(patch, out);
                    } else {
                        break;
                    }
                }
            }
        }
        return tester.patch(patch);
    }

    private static int sum(int[] array) {
        int sum = 0;
        for (int e : array) {
            sum += e;
        }
        return sum;
    }

    private void paintInputRow(Graphics2D g, Iterable<Input> inputs,
            int x, int y) {
        int i = 0;
        for (Input in : inputs) {
            int h = inputHeight[i++];
            paintInput(g, in, x, y, inputWidth, h);
            y += h;
        }
    }

    private void paintOutputRow(Graphics2D g, Iterable<Output> outputs,
            int x, int y) {
        int i = 0;
        for (Output out : outputs) {
            int h = outputHeight[i++];
            paintOutput(g, out, x, y, outputWidth, h);
            y += h;
        }
    }
}
