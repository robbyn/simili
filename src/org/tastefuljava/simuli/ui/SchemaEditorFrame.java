package org.tastefuljava.simuli.ui;

import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.tastefuljava.simuli.document.DocumentIO;
import org.tastefuljava.simuli.model.Input;
import org.tastefuljava.simuli.model.Output;
import org.tastefuljava.simuli.model.Patch;
import org.tastefuljava.simuli.model.Schema;
import org.tastefuljava.simuli.util.Configuration;
import org.tastefuljava.simuli.util.Util;

public class SchemaEditorFrame extends javax.swing.JFrame {
    private static final Logger LOG
            = Logger.getLogger(SchemaEditorFrame.class.getName());
    private static final String PROP_BOUNDS = "window-bounds";
    private static final String PROP_CURRENTDIR = "current-dir";
    private static final String DOC_EXT = ".simuli";

    private Properties userSettings;
    private File currentFile;
    private InspectorDialog inspector;

    public SchemaEditorFrame() {
        initComponents();
        Schema schema = new Schema();
        Patch patch = new Patch();
        patch.setTitle("Sample Patch 1");
        patch.setPosition(-20, -20);
        patch.newInput("input 1");
        patch.newInput("input 2");
        patch.newOutput("output 1");
        patch.newOutput("output 2");
        Output out3 = patch.newOutput("output 3");
        schema.addPatch(patch);
        patch = new Patch();
        patch.setTitle("Sample Patch 2");
        patch.setPosition(300, 200);
        patch.newInput("input 1");
        Input in2 = patch.newInput("input 2");
        patch.newOutput("output 1");
        patch.newOutput("output 2");
        patch.newOutput("output 3");
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
        schemaView = new org.tastefuljava.simuli.ui.SchemaView();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        saveItem = new javax.swing.JMenuItem();
        saveAsItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        inspectorItem = new javax.swing.JCheckBoxMenuItem();

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

        fileMenu.setText("File");
        fileMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                fileMenuMenuSelected(evt);
            }
        });

        openItem.setText("Open...");
        openItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openItemActionPerformed(evt);
            }
        });
        fileMenu.add(openItem);
        fileMenu.add(jSeparator1);

        saveItem.setText("Save");
        saveItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveItem);

        saveAsItem.setText("Save as...");
        saveAsItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsItem);

        menuBar.add(fileMenu);

        viewMenu.setText("View");
        viewMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                viewMenuMenuSelected(evt);
            }
        });

        inspectorItem.setSelected(true);
        inspectorItem.setText("Object inspector");
        inspectorItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inspectorItemActionPerformed(evt);
            }
        });
        viewMenu.add(inspectorItem);

        menuBar.add(viewMenu);

        setJMenuBar(menuBar);

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

    private void saveAsItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsItemActionPerformed
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.addChoosableFileFilter(new FileFilter() {
                @Override
                public String getDescription() {
                    return "Simuli documents (*" + DOC_EXT + ")";
                }

                @Override
                public boolean accept(File file) {
                    return file.isDirectory() || file.isFile()
                            && file.getName().toLowerCase().endsWith(DOC_EXT);
                }
            });
            chooser.setCurrentDirectory(currentDir());
            chooser.setDialogTitle("Save document");
            if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(this)) {
                File file = chooser.getSelectedFile();
                if (!file.getName().endsWith(DOC_EXT)) {
                    file = new File(file.getParentFile(),
                            file.getName() + DOC_EXT);
                }
                DocumentIO.store(schemaView.getSchema(), file);
                currentFile = file;
                saveCurrentDir(chooser.getCurrentDirectory());
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            Alert.error(this, ex);
        }
    }//GEN-LAST:event_saveAsItemActionPerformed

    private void openItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openItemActionPerformed
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.addChoosableFileFilter(new FileFilter() {
                @Override
                public String getDescription() {
                    return "Simuli documents (*" + DOC_EXT + ")";
                }

                @Override
                public boolean accept(File file) {
                    return file.isDirectory() || file.isFile()
                            && file.getName().toLowerCase().endsWith(DOC_EXT);
                }
            });
            chooser.setCurrentDirectory(currentDir());
            chooser.setDialogTitle("Open document");
            if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
                File file = chooser.getSelectedFile();
                schemaView.setSchema(DocumentIO.load(file));
                currentFile = file;
                saveCurrentDir(chooser.getCurrentDirectory());
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            Alert.error(this, ex);
        }
    }//GEN-LAST:event_openItemActionPerformed

    private void saveItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveItemActionPerformed
        try {
            if (currentFile != null) {
                DocumentIO.store(schemaView.getSchema(), currentFile);
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            Alert.error(this, ex);
        }
    }//GEN-LAST:event_saveItemActionPerformed

    private void fileMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_fileMenuMenuSelected
        saveItem.setEnabled(currentFile != null);
    }//GEN-LAST:event_fileMenuMenuSelected

    private void viewMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_viewMenuMenuSelected
        inspectorItem.setSelected(inspector != null && inspector.isVisible());
    }//GEN-LAST:event_viewMenuMenuSelected

    private void inspectorItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inspectorItemActionPerformed
        if (inspector == null) {
            inspector = new InspectorDialog(this, false);
        }
        inspector.setVisible(!inspector.isVisible());
    }//GEN-LAST:event_inspectorItemActionPerformed

    private File currentDir() {
        String s = userSettings.getProperty(PROP_CURRENTDIR);
        if (s == null) {
            return Configuration.userHome();
        }
        return new File(s);
    }

    private void saveCurrentDir(File dir) {
        userSettings.setProperty(PROP_CURRENTDIR, dir.getAbsolutePath());
        saveUserSettings();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu fileMenu;
    private javax.swing.JCheckBoxMenuItem inspectorItem;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openItem;
    private javax.swing.JMenuItem saveAsItem;
    private javax.swing.JMenuItem saveItem;
    private org.tastefuljava.simuli.ui.SchemaView schemaView;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JMenu viewMenu;
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
