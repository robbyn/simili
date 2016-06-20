package org.tastefuljava.simuli.render;

import org.tastefuljava.simuli.model.Input;
import org.tastefuljava.simuli.model.Output;
import org.tastefuljava.simuli.model.Patch;

public interface HitTester<T> {
    default public T patchTitle(Patch patch) {
        return null;
    }

    default public T patch(Patch patch) {
        return null;
    }

    default public T inputPin(Patch patch, Input in) {
        return null;
    }

    default public T inputName(Patch patch, Input in) {
        return null;
    }

    default public T outputPin(Patch patch, Output out) {
        return null;
    }

    default public T outputName(Patch patch, Output out) {
        return null;
    }

    default public T background() {
        return null;
    }
}
