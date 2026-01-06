package Replacement;

import Model.*;

public class OptimalReplacementStrategy implements ReplacementStrategy
{
    private int[] accessSequence;
    private int nextIndex;

    public void setAccessSequence(int[] accessSequence) {
        this.accessSequence = accessSequence;
    }

    public void setNextIndex(int nextIndex) {
        this.nextIndex = nextIndex;
    }

    @Override
    public int chooseVictim(PageTable pageTable) {
        if (accessSequence == null) {
            throw new IllegalStateException("OPT needs accessSequence to be set.");
        }

        int victimPage = -1;
        int farthestNextUse = -1; // cu cât mai mare, cu atât mai "bună" victimă

        for (PageTableEntry entry : pageTable.getEntries()) {
            if (!entry.isInRAM()) continue;

            int page = entry.getPageNumber();
            int nextUse = findNextUse(page);

            // daca nu apare in viitor o scot imediat (setez ca victima)
            if (nextUse == Integer.MAX_VALUE) {
                return page;
            }

            // pagina cu folosirea cea mai indepartata
            if (nextUse > farthestNextUse) {
                farthestNextUse = nextUse;
                victimPage = page;
            }
        }

        return victimPage;
    }

    private int findNextUse(int page) {
        for (int i = nextIndex; i < accessSequence.length; i++) {
            if (accessSequence[i] == page) {
                return i;
            }
        }
        return Integer.MAX_VALUE; // nu mai apare
    }

}
