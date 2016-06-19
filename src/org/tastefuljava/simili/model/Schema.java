package org.tastefuljava.simili.model;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

public class Schema implements Iterable<Patch> {
    private final Deque<Patch> patches = new LinkedList<>();

    @Override
    public Iterator<Patch> iterator() {
        return patches.descendingIterator();
    }

    public void addPatch(Patch patch) {
        patches.add(patch);
    }
}
