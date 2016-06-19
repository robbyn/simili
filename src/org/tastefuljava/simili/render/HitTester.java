package org.tastefuljava.simili.render;

import org.tastefuljava.simili.model.Input;
import org.tastefuljava.simili.model.Output;
import org.tastefuljava.simili.model.Patch;

public interface HitTester<T> {
    public T patchTitle(Patch patch);
    public T patch(Patch patch);
    public T inputPin(Patch patch, Input in);
    public T inputName(Patch patch, Input in);
    public T outputPin(Patch patch, Output out);
    public T outputName(Patch patch, Output out);
    public T background();
}
