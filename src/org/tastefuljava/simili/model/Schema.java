package org.tastefuljava.simili.model;

import org.tastefuljava.simili.geometry.PaintContext;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class Schema {
    private final List<Patch> patches = new ArrayList<>();

    public void addPatch(Patch patch) {
        patches.add(patch);
    }

    public Iterable<Patch> visiblePatches(int x, int y, int w, int h,
            PaintContext pc) {
        int right = x + w;
        int bottom = y + h;
        List<Patch> result = new ArrayList<>();
        for (Patch patch: patches) {
            int px = patch.getX();
            int py = patch.getY();
            if (px < right && py < bottom) {
                Dimension dim = pc.patchSize(patch);
                int pr = px + dim.width;
                int pb = py + dim.height;
                if (pr > x && pb > y) {
                    result.add(patch);
                }
            }
        }
        return result;
    }

    public Iterable<Input> visibleConnections(Iterable<Patch> patches,
            int x, int y, int w, int h, PaintContext pc) {
        List<Input> result = new ArrayList<>();
        
        return result;
    }
}
