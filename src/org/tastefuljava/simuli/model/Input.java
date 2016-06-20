package org.tastefuljava.simuli.model;

public class Input extends Pin {
    private Output source;

    @Deprecated // for persistence only
    public Input() {
        super(null, null);
    }

    protected Input(Patch patch, String name) {
        super(patch, name);
    }

    @Override
    public boolean isConnected() {
        return source != null;
    }

    @Override
    public int getIndex() {
        return patch.inputIndexOf(this);
    }

    public Output getSource() {
        return source;
    }

    public void setSource(Output newSource) {
        if (source != null) {
            source.removeSink(this);
        }
        source = newSource;
        if (source != null) {
            source.addSink(this);
        }
    }

    @Override
    protected void detach() {
        setSource(null);
    }
}
