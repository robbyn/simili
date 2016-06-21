package org.tastefuljava.simuli.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.tastefuljava.simuli.model.Pin;

public class PinTableModel extends AbstractTableModel {
    private static final int COL_INDEX = 0;
    private static final int COL_NAME = 1;
    private static final int COL_COUNT = 2;

    private final List<Pin> pins = new ArrayList<>();

    public PinTableModel(Iterable<? extends Pin> pins) {
        if (pins != null) {
            for (Pin pin: pins) {
                this.pins.add(pin);
            }
        }
    }

    @Override
    public int getRowCount() {
        return pins.size();
    }

    @Override
    public int getColumnCount() {
        return COL_COUNT;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case COL_INDEX:
                return "#";
            case COL_NAME:
                return "Name";
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COL_INDEX:
                return int.class;
            case COL_NAME:
                return String.class;
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return rowIndex >= 0 && rowIndex < pins.size()
                && columnIndex == COL_NAME;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case COL_INDEX:
                return rowIndex+1;
            case COL_NAME:
                return pins.get(rowIndex).getName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case COL_NAME:
                pins.get(rowIndex).setName(aValue + "");
                this.fireTableCellUpdated(rowIndex, columnIndex);
                break;
        }
    }   
}
