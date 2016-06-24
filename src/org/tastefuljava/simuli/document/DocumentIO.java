package org.tastefuljava.simuli.document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.tastefuljava.simuli.model.Schema;
import org.xml.sax.SAXException;

public class DocumentIO {
    private static final Logger LOG
            = Logger.getLogger(DocumentIO.class.getName());

    public static Schema load(File file) throws IOException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(true);
            SAXParser parser = factory.newSAXParser();
            DocumentReader reader = new DocumentReader();
            parser.parse(file, reader);
            return reader.getSchema();
        } catch (SAXException | ParserConfigurationException e) {
            LOG.log(Level.SEVERE, "Error reading project", e);
            throw new IOException(e.getMessage());
        }
    }

    public static void store(Schema schema, File file) throws IOException {
        try (OutputStream stream = new FileOutputStream(file);
                Writer writer = new OutputStreamWriter(stream, "UTF-8");
                PrintWriter pwriter = new PrintWriter(writer);
                DocumentWriter out = new DocumentWriter(pwriter)) {
            out.writeSchema(schema);
        }
    }
}
