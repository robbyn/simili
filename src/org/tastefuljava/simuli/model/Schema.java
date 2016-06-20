package org.tastefuljava.simuli.model;

import java.awt.Point;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Schema {
    private final Deque<Patch> patches = new LinkedList<>();

    public Schema() {
    }

    public Schema(Schema other) {
        assign(other);
    }

    private void assign(Schema other) {
        Map<Input,Input> inputs = new HashMap<>();
        Map<Output,Output> outputs = new HashMap<>();
        for (Patch otherPatch: other.patches()) {
            Patch patch = new Patch(otherPatch);
            assert patch.getInputCount() == otherPatch.getOutputCount()
                    && patch.getOutputCount() == otherPatch.getOutputCount();
            addPatch(patch);
            for (int i = 0; i < patch.getInputCount(); ++i) {
                inputs.put(otherPatch.getInput(i), patch.getInput(i));
            }
            for (int i = 0; i < patch.getOutputCount(); ++i) {
                outputs.put(otherPatch.getOutput(i), patch.getOutput(i));
            }
        }
        for (Map.Entry<Input,Input> e: inputs.entrySet()) {
            Input otherIn = e.getKey();
            Input in = e.getValue();
            Output otherOut = otherIn.getSource();
            if (otherOut != null) {
                in.setSource(outputs.get(otherOut));
            }
        }
    }

    public Iterable<Patch> patches() {
        return () -> patches.iterator();
    }

    public Iterable<Patch> descending() {
        return () -> patches.descendingIterator();
    }

    public Point getLeftTop() {
        if (patches.isEmpty()) {
            return new Point(0,0);
        } else {
            int left = Integer.MAX_VALUE;
            int top = Integer.MAX_VALUE;
            for (Patch patch: patches) {
                int x = patch.getX();
                int y = patch.getY();
                if (x < left) {
                    left = x;
                }
                if (y < top) {
                    top = y;
                }
            }
            return new Point(left, top);
        }
    }

    public void addPatch(Patch patch) {
        patches.add(patch);
    }
}
