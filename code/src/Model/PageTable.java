package Model;

import java.util.ArrayList;
import java.util.List;

public class PageTable {
    private final List<PageTableEntry> entries;

    public PageTable(int numberOfPages) {
        entries = new ArrayList<>();
        for (int i = 0; i < numberOfPages; i++) {
            entries.add(new PageTableEntry(i));
        }
    }

    public PageTableEntry getEntry(int pageNumber) {
        return entries.get(pageNumber);
    }

    public List<PageTableEntry> getEntries() {
        return entries;
    }
}
