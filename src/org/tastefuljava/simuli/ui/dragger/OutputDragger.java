package org.tastefuljava.simuli.ui.dragger;

import org.tastefuljava.simuli.model.Input;
import org.tastefuljava.simuli.model.Output;
import org.tastefuljava.simuli.render.Grabbers;
import org.tastefuljava.simuli.render.RenderContext;
import org.tastefuljava.simuli.ui.SchemaView;

public class OutputDragger extends PinDragger<Output> {
    public OutputDragger(SchemaView view, Output out) {
        super(view, out, RenderContext.current().outputPosition(out));
    }

    @Override
    protected void connect() {
        Input in = view.hitTest(x, y, Grabbers.INPUT_GRABBER);
        if (in != null) {
            in.setSource(pin);
        }
    }
}
