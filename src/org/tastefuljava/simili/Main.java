package org.tastefuljava.simili;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tastefuljava.simili.ui.SchemaEditorFrame;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        LOG.info("Starting Simili");
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new SchemaEditorFrame().display();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        });
    }    
}
