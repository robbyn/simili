package org.tastefuljava.simuli.ui;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.tastefuljava.simuli.model.Patch;

public class InspectorPanel extends javax.swing.JPanel {
    private static final Logger LOG
            = Logger.getLogger(InspectorPanel.class.getName());

    private Patch patch;

    public InspectorPanel() {
        initComponents();
        title.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed(e.getDocument());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changed(e.getDocument());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changed(e.getDocument());
            }

            private void changed(Document doc) {
                try {
                    titleChanged(doc.getText(0, doc.getLength()));
                } catch (BadLocationException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    void bindPatch(Patch patch) {
        this.patch = patch;
        TableModel inputModel;
        TableModel outputModel;
        if (patch == null) {
            title.setText("");
            inputModel = new PinTableModel(null);
            outputModel = new PinTableModel(null);
        } else {
            title.setText(patch.getTitle());
            inputModel = new PinTableModel(patch.getInputs());
            outputModel = new PinTableModel(patch.getOutputs());
        }
        inputTable.setModel(inputModel);
        outputTable.setModel(outputModel);
    }

    private void titleChanged(String newValue) {
        if (patch != null) {
            patch.setTitle(newValue);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        patchPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        title = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        inputTable = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        outputTable = new javax.swing.JTable();

        setLayout(new java.awt.CardLayout());

        patchPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Patch properties");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        patchPanel.add(jLabel1, gridBagConstraints);

        jLabel2.setText("Title:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 11);
        patchPanel.add(jLabel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 11);
        patchPanel.add(title, gridBagConstraints);

        jLabel3.setText("Inputs:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 11);
        patchPanel.add(jLabel3, gridBagConstraints);

        inputTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 11);
        patchPanel.add(inputTable, gridBagConstraints);

        jLabel4.setText("Outputs:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 11);
        patchPanel.add(jLabel4, gridBagConstraints);

        outputTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 11, 11);
        patchPanel.add(outputTable, gridBagConstraints);

        add(patchPanel, "patch");
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable inputTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTable outputTable;
    private javax.swing.JPanel patchPanel;
    private javax.swing.JTextField title;
    // End of variables declaration//GEN-END:variables
}
