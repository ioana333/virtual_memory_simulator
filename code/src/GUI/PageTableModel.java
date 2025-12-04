package GUI;

import Model.*;
import Controller.*;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class PageTableModel extends AbstractTableModel {

    private final String[] columnNames = {
            "Pagina", "inRAM", "Frame", "LastUsed", "Loaded", "Dirty"
    };

    private MemoryManager memoryManager;

    public PageTableModel(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
    }

    public void setMemoryManager(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        if (memoryManager == null) return 0;
        return memoryManager.getPageTable().getEntries().size();
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

        List<PageTableEntry> entries = memoryManager.getPageTable().getEntries();
        PageTableEntry e = entries.get(rowIndex);

        switch (columnIndex) {
            case 0: return e.getPageNumber();
            case 1: return e.isInRAM();
            case 2: return e.getFrameNumber();
            case 3: return e.getLastUsedTime();
            case 4: return e.getLoadedTime();
            case 5: return e.isDirty();
            default: return null;
        }
    }
}
