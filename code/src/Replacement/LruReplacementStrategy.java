package Replacement;

import Model.PageTable;
import Model.PageTableEntry;

public class LruReplacementStrategy implements ReplacementStrategy {

    @Override
    public int chooseVictim(PageTable pageTable) {
        int victimPage = -1;
        int oldestUse = Integer.MAX_VALUE;

        for (PageTableEntry entry : pageTable.getEntries()) {
            if (entry.isInRAM()) {
                if (entry.getLastUsedTime() < oldestUse) {
                    oldestUse = entry.getLastUsedTime();
                    victimPage = entry.getPageNumber();
                }
            }
        }

        return victimPage;
    }
}
