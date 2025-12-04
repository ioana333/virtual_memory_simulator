package Replacement;

import Model.PageTable;
import Model.PageTableEntry;

public class FifoReplacementStrategy implements ReplacementStrategy {

    @Override
    public int chooseVictim(PageTable pageTable) {
        int victimPage = -1;
        int oldestTime = Integer.MAX_VALUE;

        for (PageTableEntry entry : pageTable.getEntries()) {
            if (entry.isInRAM()) {
                if (entry.getLoadedTime() < oldestTime) {
                    oldestTime = entry.getLoadedTime();
                    victimPage = entry.getPageNumber();
                }
            }
        }

        return victimPage;
    }
}
