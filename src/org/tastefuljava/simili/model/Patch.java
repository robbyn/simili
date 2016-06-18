package org.tastefuljava.simili.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Patch implements Serializable {
    private String title;
    private int x;
    private int y;
    private final List<Input> inputs = new ArrayList<>();
    private final List<Output> outputs = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getInputCount() {
        return inputs.size();
    }

    public Input getInput(int i) {
        return inputs.get(i);
    }

    public int inputIndexOf(Input in) {
        return inputs.indexOf(in);
    }

    public Iterable<Input> getInputs() {
        return inputs;
    }

    public Input newInput(String name) {
        Input input = new Input(this, name);
        inputs.add(input);
        return input;
    }

    public void removeInput(int i) {
        Input in = inputs.remove(i);
        if (in != null) {
            in.detach();
        }
    }

    public boolean removeInput(Input in) {
        boolean done = inputs.remove(in);
        if (done) {
            in.detach();
        }
        return done;
    }

    public int getOutputCount() {
        return outputs.size();
    }

    public Output getOutput(int i) {
        return outputs.get(i);
    }

    public int outputIndexOf(Output in) {
        return outputs.indexOf(in);
    }

    public Iterable<Output> getOutputs() {
        return outputs;
    }

    public Output newOutput(String name) {
        Output input = new Output(this, name);
        outputs.add(input);
        return input;
    }

    public void removeOutput(int i) {
        Output in = outputs.remove(i);
        if (in != null) {
            in.detach();
        }
    }

    public boolean removeOutput(Output in) {
        boolean done = outputs.remove(in);
        if (done) {
            in.detach();
        }
        return done;
    }
}
