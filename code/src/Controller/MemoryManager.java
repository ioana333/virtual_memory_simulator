package Controller;

import Model.*;
import Replacement.ReplacementStrategy;

public class MemoryManager {

    private final PageTable pageTable;
    private final PhysicalMemory physicalMemory;
    private final ReplacementStrategy replacementStrategy;

    private int timeCounter = 0;      // crește la fiecare acces (pentru LRU / FIFO)
    private int pageFaultCount = 0;

    public MemoryManager(int numberOfPages, int numberOfFrames,
                         ReplacementStrategy replacementStrategy) {
        this.pageTable = new PageTable(numberOfPages);
        this.physicalMemory = new PhysicalMemory(numberOfFrames);
        this.replacementStrategy = replacementStrategy;
    }

    /**
     * Simuleaza accesul la o pagina virtuala
     */
    public AccessResult accessPage(int pageNumber) {
        timeCounter++;
        PageTableEntry entry = pageTable.getEntry(pageNumber);

        if (entry.isInRAM()) {
            // HIT
            entry.setLastUsedTime(timeCounter);
            return new AccessResult(pageNumber, true, null, pageFaultCount, timeCounter);
        } else {
            // PAGE FAULT
            pageFaultCount++;
            Integer victimPage = handlePageFault(pageNumber);
            return new AccessResult(pageNumber, false, victimPage, pageFaultCount, timeCounter);
        }
    }

    private Integer handlePageFault(int pageNumber) {
        PageTableEntry entry = pageTable.getEntry(pageNumber);

        // 1. cauta un frame liber
        Frame freeFrame = physicalMemory.findFreeFrame();
        if (freeFrame != null) {
            loadPageIntoFrame(entry, freeFrame);
            return null; // nu am înlocuit pe nimeni
        }

        // 2. Nu avem frame liber → alegem victima
        int victimPageNumber = replacementStrategy.chooseVictim(pageTable);
        PageTableEntry victimEntry = pageTable.getEntry(victimPageNumber);
        Frame victimFrame = physicalMemory.getFrame(victimEntry.getFrameNumber());

        // aici, teoretic: dacă victimEntry.isDirty() -> scriem pe disc (simulat)

        // eliberăm pagina victima
        victimEntry.setInRAM(false);
        victimEntry.setFrameNumber(-1);

        // încărcăm noua pagina in frame-ul victimei
        loadPageIntoFrame(entry, victimFrame);

        return victimPageNumber;
    }

    private void loadPageIntoFrame(PageTableEntry entry, Frame frame) {
        frame.setPageNumber(entry.getPageNumber());
        entry.setInRAM(true);
        entry.setFrameNumber(frame.getFrameNumber());
        entry.setLastUsedTime(timeCounter);
        entry.setLoadedTime(timeCounter);
        entry.setDirty(false); // la încărcare considerăm că nu e murdară
    }

    public PageTable getPageTable() {
        return pageTable;
    }

    public PhysicalMemory getPhysicalMemory() {
        return physicalMemory;
    }

    public int getPageFaultCount() {
        return pageFaultCount;
    }
}
