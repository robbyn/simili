package org.tastefuljava.simuli.ui.dragger;

import org.tastefuljava.simuli.model.Input;
import org.tastefuljava.simuli.model.Output;
import org.tastefuljava.simuli.render.Grabbers;
import org.tastefuljava.simuli.render.RenderContext;
import org.tastefuljava.simuli.ui.SchemaView;

public class InputDragger extends PinDragger<Input> {
    public InputDragger(SchemaView view, Input in) {
        super(view, in, RenderContext.current().inputPosition(in));
    }

    @Override
    protected void connect() {
        Output out = view.hitTest(x, y, Grabbers.OUTPUT_GRABBER);
        if (out != null) {
            pin.setSource(out);
        }
    }
}
