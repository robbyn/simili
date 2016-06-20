package org.tastefuljava.simuli.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

public class XMLWriter {
    private static enum State {
	INITIAL, AFTERSTART, AFTERATTRIBUTE, AFTEREND, AFTERTEXT;
    }

    private PrintWriter out;
    private State state = State.INITIAL;
    private int level;

    public XMLWriter(PrintWriter out) {
        this.out = out;
    }

    public XMLWriter(Writer out) {
        this(new PrintWriter(out));
    }

    public XMLWriter(OutputStream out) {
        this(new OutputStreamWriter(out));
    }

    public XMLWriter(OutputStream out, String encoding) throws IOException {
        this(new OutputStreamWriter(out, encoding));
    }

    public XMLWriter(File file) throws IOException {
        this(new FileOutputStream(file));
    }

    public XMLWriter(File file, String encoding) throws IOException {
        this(new FileOutputStream(file), encoding);
    }

    public void close() {
        if (out != null) {
            out.close();
            out = null;
        }
    }

    public void start(String name) {
        switch (state) {
        case AFTERSTART:
        case AFTERATTRIBUTE:
            out.print(">");
            // no break
        case AFTEREND:
            out.println();
            indent(level);
            break;
        }
        out.print("<");
        out.print(name);
        ++level;
        state = State.AFTERSTART;
    }

    public void end(String name) {
        --level;
        switch (state) {
        case AFTERSTART:
        case AFTERATTRIBUTE:
            out.print("/>");
            break;
        case AFTEREND:
            out.println();
            indent(level);
            // no break
        default:
            out.print("</");
            out.print(name);
            out.print(">");
        }
        state = State.AFTEREND;
    }

    public void attribute(String name, String value) {
        if (value != null) {
        	switch (state) {
            case AFTERSTART:
                out.print(" ");
                break;
            case AFTERATTRIBUTE:
                out.println();
                indent(level + 1);
                break;
            default:
            	throw new IllegalStateException("Cannot write an attribute here");
        	}
            out.print(name);
            out.print("=\"");
            writeValue(value);
            out.print("\"");
            state = State.AFTERATTRIBUTE;
        }
    }

    public void attribute(String name, Object obj) {
        if (obj != null) {
            attribute(name, obj.toString());
        }
    }

    public void attribute(String name, long value) {
        attribute(name, Long.toString(value));
    }

    public void attribute(String name, double value) {
        attribute(name, Util.dbl2str(value));
    }

    public void attribute(String name, boolean value) {
        attribute(name, Boolean.toString(value));
    }

    public void text(String text) {
    	switch (state) {
    	case AFTERSTART:
    	case AFTERATTRIBUTE:
            out.print(">");
            break;
    	case AFTEREND:
            out.println();
            indent(level);
            break;
    	}
        writeValue(text);
        state = State.AFTERTEXT;
    }

    private void indent(int n) {
        for (int i = 0; i < n; ++i) {
            out.print("    ");
        }
    }

    private void writeValue(String value) {
        char chars[] = value.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            switch (c) {
            case '<':
                out.print("&lt;");
                break;
            case '>':
                out.print("&gt;");
                break;
            case '"':
                out.print("&quot;");
                break;
            case '&':
                out.print("&amp;");
                break;
            case '\n':
                out.println();
                indent(level + 1);
                break;
            default:
                out.print(c);
                break;
            }
        }
    }
}
