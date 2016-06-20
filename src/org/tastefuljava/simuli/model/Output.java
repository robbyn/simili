package org.tastefuljava.simuli.model;

import java.util.HashSet;
import java.util.Set;

public class Output extends Pin {
    private final Set<Input> sinks = new HashSet<>();

    @Deprecated // for persistence only
    public Output() {
        super(null, null);
    }

    protected Output(Patch patch, String name) {
        super(patch, name);
    }

    @Override
    public boolean isConnected() {
        return !sinks.isEmpty();
    }

    @Override
    public int getIndex() {
        return patch.outputIndexOf(this);
    }

    public int getSinkCount() {
        return sinks.size();
    }

    public Iterable<Input> getSinks() {
        return sinks;
    }

    boolean addSink(Input sink) {
        assert sink.getSource() == this;
        return sinks.add(sink);
    }

    boolean removeSink(Input sink) {
        assert sink.getSource() == this;
        return sinks.remove(sink);
    }

    @Override
    protected void detach() {
        final Input[] array = sinks.toArray(new Input[sinks.size()]);
        for (Input sink: array) {
            sink.detach();
        }
        assert sinks.isEmpty();
    }
}
