package org.tastefuljava.simili.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Schema implements Iterable<Patch> {
    private final List<Patch> patches = new ArrayList<>();

    @Override
    public Iterator<Patch> iterator() {
        return patches.iterator();
    }

    public void addPatch(Patch patch) {
        patches.add(patch);
    }
}
