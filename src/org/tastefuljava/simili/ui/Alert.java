package org.tastefuljava.simili.ui;

import java.awt.Component;
import javax.swing.JOptionPane;

public class Alert {
    public static void error(Component comp, String message) {
        JOptionPane.showMessageDialog(comp, message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void error(Component comp, Throwable e) {
        error(comp, e.getMessage());
    }

    public static void info(Component comp, String message, String title) {
        JOptionPane.showMessageDialog(comp, message, title,
                JOptionPane.INFORMATION_MESSAGE);
    }
}
