package org.tastefuljava.simili.ui;

import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tastefuljava.simili.model.Input;
import org.tastefuljava.simili.model.Output;
import org.tastefuljava.simili.model.Patch;
import org.tastefuljava.simili.model.Schema;
import org.tastefuljava.simili.util.Configuration;
import org.tastefuljava.simili.util.Util;

public class SchemaEditorFrame extends javax.swing.JFrame {
    private static final Logger LOG
            = Logger.getLogger(SchemaEditorFrame.class.getName());
    private static final String PROP_BOUNDS = "window-bounds";

    private Properties userSettings;

    public SchemaEditorFrame() {
        initComponents();
        Schema schema = new Schema();
        Patch patch = new Patch();
        patch.setTitle("Sample Patch");
        patch.setPosition(-20, -20);
        patch.newInput("input 1");
        Input in2 = patch.newInput("input 2");
        patch.newOutput("output 1");
        patch.newOutput("output 2");
        Output out3 = patch.newOutput("output 3");
        in2.setSource(out3);
        schema.addPatch(patch);
        schemaView.setSchema(schema);
    }

    public void display() throws IOException {
        userSettings = Configuration.loadUserSettings();
        String s = userSettings.getProperty(PROP_BOUNDS);
        Rectangle rc = s == null
                ? defaultBounds() : Util.parseRect(s);
        setBounds(rc);
        setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        schemaView = new org.tastefuljava.simili.ui.SchemaView();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                formComponentMoved(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        scrollPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                scrollPaneComponentResized(evt);
            }
        });
        scrollPane.setViewportView(schemaView);

        getContentPane().add(scrollPane);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentMoved
        saveBounds();
    }//GEN-LAST:event_formComponentMoved

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        saveBounds();
    }//GEN-LAST:event_formComponentResized

    private void scrollPaneComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_scrollPaneComponentResized
        schemaView.updateSize();
    }//GEN-LAST:event_scrollPaneComponentResized

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.tastefuljava.simili.ui.SchemaView schemaView;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables

    private Rectangle defaultBounds() {
        Rectangle rc = getGraphicsConfiguration().getBounds();
        int width = rc.width/2;
        int height = rc.height/2;
        return new Rectangle(rc.x + width/2, rc.y + height/2, width, height);
    }

    private void saveBounds() {
        if (userSettings != null) {
            userSettings.put(PROP_BOUNDS, Util.formatRect(getBounds()));
            saveUserSettings();
        }
    }

    private void saveUserSettings() throws HeadlessException {
        try {
            Configuration.saveUserSettings(userSettings);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            Alert.error(this, ex);
        }
    }
}
