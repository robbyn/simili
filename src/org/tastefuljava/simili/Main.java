package org.tastefuljava.simili;

import java.awt.EventQueue;
import org.tastefuljava.simili.ui.SchemaEditorFrame;

public class Main {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SchemaEditorFrame().setVisible(true);
            }
        });
    }    
}
