package org.tastefuljava.simuli.ui;

import java.awt.CardLayout;
import org.tastefuljava.simuli.model.Patch;
import org.tastefuljava.simuli.util.ListenerList;

public class InspectorPanel extends javax.swing.JPanel {
    private final ListenerList listeners = new ListenerList();
    private final ChangeListener changeNotifier
            = listeners.getNotifier(ChangeListener.class);

    private final CardLayout layout;

    public InspectorPanel() {
        initComponents();
        layout = getCardLayout();
        patchPanel.addChangeListener(changeNotifier);
        setSelection(null);
    }

    private CardLayout getCardLayout() {
        return (CardLayout)getLayout();
    }

    public void addChangeListener(ChangeListener listener) {
        listeners.addListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        listeners.removeListener(listener);
    }

    public final void setSelection(Patch[] selection) {
        if (selection == null || selection.length == 0) {
            patchPanel.bindPatch(null);
            layout.show(this, "none");
        } else if (selection.length == 1) {
            patchPanel.bindPatch(selection[0]);
            layout.show(this, "patch");
        } else {
            patchPanel.bindPatch(null);
            layout.show(this, "multiple");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        patchPanel = new org.tastefuljava.simuli.ui.PatchInspectorPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();

        setLayout(new java.awt.CardLayout());
        add(patchPanel, "patch");

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Nothing selected");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 11);
        jPanel2.add(jLabel1, gridBagConstraints);

        jPanel1.add(jPanel2, java.awt.BorderLayout.NORTH);

        add(jPanel1, "none");

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Multiple selection");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 11);
        jPanel4.add(jLabel2, gridBagConstraints);

        jPanel3.add(jPanel4);

        add(jPanel3, "multiple");
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private org.tastefuljava.simuli.ui.PatchInspectorPanel patchPanel;
    // End of variables declaration//GEN-END:variables
}
