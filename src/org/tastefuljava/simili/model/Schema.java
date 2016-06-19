package org.tastefuljava.simili.model;

import java.awt.Point;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

public class Schema implements Iterable<Patch> {
    private final Deque<Patch> patches = new LinkedList<>();

    @Override
    public Iterator<Patch> iterator() {
        return patches.descendingIterator();
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
