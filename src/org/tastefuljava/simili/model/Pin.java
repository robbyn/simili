package org.tastefuljava.simili.model;

import java.io.Serializable;

public abstract class Pin implements Serializable {
    protected final Patch patch;
    protected String name;

    protected Pin(Patch patch, String name) {
        this.patch = patch;
        this.name = name;
    }

    public abstract boolean isConnected();
    public abstract int getIndex();
    protected abstract void detach();

    public Patch getPatch() {
        return patch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
