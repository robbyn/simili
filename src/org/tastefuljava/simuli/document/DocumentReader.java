package org.tastefuljava.simuli.document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tastefuljava.simuli.model.Input;
import org.tastefuljava.simuli.model.Output;
import org.tastefuljava.simuli.model.Patch;
import org.tastefuljava.simuli.model.Schema;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

class DocumentReader extends DefaultHandler {
    private static final Logger LOG
            = Logger.getLogger(DocumentReader.class.getName());

    private static final String DTD_SYSTEM_ID = "simuli.dtd";
    private static final String DTD_PUBLIC_ID
            = "-//tastefuljava.org//Simuli Document 1.0//EN";

    private Schema schema;
    private Patch patch;
    private StringBuilder buf;
    private Map<String,Input> inputs = new HashMap<>();
    private Map<String,Output> outputs = new HashMap<>();

    Schema getSchema() {
        return schema;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws IOException, SAXException {
        if (DTD_PUBLIC_ID.equals(publicId)
                || DTD_SYSTEM_ID.equals(systemId)) {
            InputSource source = new InputSource(
                    getClass().getResourceAsStream(DTD_SYSTEM_ID));
            source.setPublicId(publicId);
            source.setSystemId(systemId);
            return source;
        }
        return super.resolveEntity(publicId, systemId);
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        LOG.log(Level.SEVERE, e.getMessage(), e);
        throw new SAXException(e.getMessage());
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        LOG.log(Level.SEVERE, e.getMessage(), e);
        throw new SAXException(e.getMessage());
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        LOG.log(Level.WARNING, e.getMessage(), e);
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attrs) throws SAXException {
        switch (qName) {
            case "schema":
                schema = new Schema();
                break;
            case "patch":
                startPatch(attrs);
                break;
            case "input":
                startInput(attrs);
                break;
            case "output":
                startOutput(attrs);
                break;
            case "link":
                startLink(attrs);
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        switch (qName) {
            case "patch":
                schema.addPatch(patch);
                patch = null;
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (buf != null) {
            buf.append(ch, start, length);
        }
    }

    private void startPatch(Attributes attrs) throws NumberFormatException {
        patch = new Patch();
        patch.setTitle(attrs.getValue("title"));
        patch.setPosition(Integer.parseInt(attrs.getValue("x")),
                Integer.parseInt(attrs.getValue("y")));
    }

    private void startInput(Attributes attrs) {
        Input in = patch.newInput(attrs.getValue("name"));
        String id = attrs.getValue("id");
        if (id != null) {
            inputs.put(id, in);
        }
    }

    private void startOutput(Attributes attrs) {
        Output out = patch.newOutput(attrs.getValue("name"));
        String id = attrs.getValue("id");
        if (id != null) {
            outputs.put(id, out);
        }
    }

    private void startLink(Attributes attrs) throws SAXException {
        String from = attrs.getValue("from");
        Output out = outputs.get(from);
        if (out == null) {
            throw new SAXException("Output ID not found: [" + from + "]");
        }
        String to = attrs.getValue("to");
        for (String id: to.split("\\s")) {
            Input in = inputs.get(id);
            if (in == null) {
                throw new SAXException("Input ID not found: [" + id + "]");
            }
            in.setSource(out);
        }
    }
}
