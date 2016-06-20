package org.tastefuljava.simuli.document;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.IdentityHashMap;
import java.util.Map;
import org.tastefuljava.simuli.model.Input;
import org.tastefuljava.simuli.model.Output;
import org.tastefuljava.simuli.model.Patch;
import org.tastefuljava.simuli.model.Pin;
import org.tastefuljava.simuli.model.Schema;
import org.tastefuljava.simuli.util.XMLWriter;

class DocumentWriter implements Closeable {
    private final PrintWriter out;
    private final XMLWriter xml;

    private final Map<Input,String> inputIds = new IdentityHashMap<>();
    private final Map<Output,String> outputIds = new IdentityHashMap<>();
    private int lastId = 0;

    DocumentWriter(PrintWriter out) {
        this.out = out;
        this.xml = new XMLWriter(out);
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<!DOCTYPE schema"
                + " PUBLIC \"-//tastefuljava.org//Simuli Document 1.0//EN\""
                + " \"simuli.dtd\">");
    }

    @Override
    public void close() throws IOException {
        try {
            xml.close();
        } finally {
            out.close();
        }
    }

    void writeSchema(Schema schema) {
        xml.start("schema");
        for (Patch patch: schema.patches()) {
            writePatch(patch);
        }
        for (Patch patch: schema.patches()) {
            for (Output out: patch.getOutputs()) {
                if (out.isConnected()) {
                    writeLinks(out);
                }
            }
        }
        xml.end("schema");
    }

    private void writePatch(Patch patch) {
        int id = ++lastId;
        xml.start("patch");
        xml.attribute("type", patch.getClass().getName());
        xml.attribute("title", patch.getTitle());
        xml.attribute("x", Integer.toString(patch.getX()));
        xml.attribute("y", Integer.toString(patch.getY()));
        for (Input pin: patch.getInputs()) {
            writePin(id, "input", pin, inputIds);
        }
        for (Output pin: patch.getOutputs()) {
            writePin(id, "output", pin, outputIds);
        }
        xml.end("patch");
    }

    private <T extends Pin> void writePin(int patchId, String tag, T pin,
            Map<T,String> ids) {
        xml.start(tag);
        if (pin.isConnected()) {
            String id = "p" + patchId + tag.charAt(0) +(pin.getIndex()+1);
            xml.attribute("id", id);
            ids.put(pin, id);
        }
        xml.attribute("name", pin.getName());
        xml.end(tag);
    }

    private void writeLinks(Output out) {
        xml.start("link");
        xml.attribute("from", outputIds.get(out));
        xml.attribute("to", inputList(out.getSinks()));
        xml.end("link");
    }

    private String inputList(Iterable<Input> inputs) {
        StringBuilder buf = new StringBuilder();
        for (Input in: inputs) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            buf.append(inputIds.get(in));
        }
        return buf.toString();
    }
}
