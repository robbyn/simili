package org.tastefuljava.simili.model;

import java.util.ArrayList;
import java.util.List;

public class Schema {
    private final List<Patch> patches = new ArrayList<>();

    public void addPatch(Patch patch) {
        patches.add(patch);
    }
}
