package org.tastefuljava.simuli.ui;

import java.awt.FontMetrics;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.tastefuljava.simuli.model.Patch;
import org.tastefuljava.simuli.util.ListenerList;

public class PatchInspectorPanel extends JPanel implements TableModelListener {
    private static final Logger LOG
            = Logger.getLogger(PatchInspectorPanel.class.getName());

    private static final PinTableModel EMPTY_PIN_TABLE_MODEL
            = new PinTableModel(null);

    private Patch patch;

    private final ListenerList listeners = new ListenerList();
    private final ChangeListener changeNotifier
            = listeners.getNotifier(ChangeListener.class);

    public PatchInspectorPanel() {
        initComponents();
        initPinTable(inputTable, EMPTY_PIN_TABLE_MODEL);
        initPinTable(outputTable, EMPTY_PIN_TABLE_MODEL);
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

    public void addChangeListener(ChangeListener listener) {
        listeners.addListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        listeners.removeListener(listener);
    }

    public void stopEditing() {
        stopEditing(inputTable);
        stopEditing(outputTable);
    }

    private void stopEditing(JTable table) {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
    }

    private void initPinTable(JTable table, TableModel model) {
        table.setModel(model);
        FontMetrics fm = table.getFontMetrics(getFont());
        table.getColumnModel().getColumn(0).setMaxWidth(fm.stringWidth("00"));
    }

    void bindPatch(Patch patch) {
        this.patch = patch;
        TableModel inputModel;
        TableModel outputModel;
        if (patch == null) {
            title.setText("");
            inputModel = EMPTY_PIN_TABLE_MODEL;
            outputModel = EMPTY_PIN_TABLE_MODEL;
        } else {
            title.setText(patch.getTitle());
            inputModel = new PinTableModel(patch.getInputs());
            inputModel.addTableModelListener(this);
            outputModel = new PinTableModel(patch.getOutputs());
            outputModel.addTableModelListener(this);
        }
        initPinTable(inputTable, inputModel);
        initPinTable(outputTable, outputModel);
    }

    private void titleChanged(String newValue) {
        if (patch != null) {
            patch.setTitle(newValue);
            changeNotifier.dataChanged();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        title = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        inputTable = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        outputTable = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Patch properties");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 11);
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel2.setText("Title:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 11);
        jPanel1.add(jLabel2, gridBagConstraints);

        title.setBorder(javax.swing.BorderFactory.createLineBorder(inputTable.getGridColor()));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 11);
        jPanel1.add(title, gridBagConstraints);

        jLabel3.setText("Inputs:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 11);
        jPanel1.add(jLabel3, gridBagConstraints);

        inputTable.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 0, 0, inputTable.getGridColor()));
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
        jPanel1.add(inputTable, gridBagConstraints);

        jLabel4.setText("Outputs:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 11);
        jPanel1.add(jLabel4, gridBagConstraints);

        outputTable.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 0, 0, outputTable.getGridColor()));
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
        jPanel1.add(outputTable, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable inputTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTable outputTable;
    private javax.swing.JTextField title;
    // End of variables declaration//GEN-END:variables

    @Override
    public void tableChanged(TableModelEvent e) {
        changeNotifier.dataChanged();
    }
}
