package GUI;

import Model.*;
import Controller.*;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class FrameTableModel extends AbstractTableModel {

    private final String[] columnNames = {
            "Frame", "Page"
    };

    private MemoryManager memoryManager;

    public FrameTableModel(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
    }

    public void setMemoryManager(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        if (memoryManager == null) return 0;
        return memoryManager.getPhysicalMemory().getFrames().size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (memoryManager == null) return null;

        List<Frame> frames = memoryManager.getPhysicalMemory().getFrames();
        Frame f = frames.get(rowIndex);

        switch (columnIndex) {
            case 0: return f.getFrameNumber();
            case 1: return f.getPageNumber();
            default: return null;
        }
    }
}
