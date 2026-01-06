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

    //simuleaza accesul
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
            return null; // nu am inlocuit pe nimeni
        }

        // 2. Nu avem frame liber → alegem victima
        int victimPageNumber = replacementStrategy.chooseVictim(pageTable);
        PageTableEntry victimEntry = pageTable.getEntry(victimPageNumber);
        Frame victimFrame = physicalMemory.getFrame(victimEntry.getFrameNumber());

        // aici, teoretic: dacă victimEntry.isDirty() -> scriem pe disc (simulat)

        // eliberare pagina victima
        victimEntry.setInRAM(false);
        victimEntry.setFrameNumber(-1);

        // incarca noua pagina in frame-ul victimei
        loadPageIntoFrame(entry, victimFrame);

        return victimPageNumber;
    }

    private void loadPageIntoFrame(PageTableEntry entry, Frame frame) {
        frame.setPageNumber(entry.getPageNumber());
        entry.setInRAM(true);
        entry.setFrameNumber(frame.getFrameNumber());
        entry.setLastUsedTime(timeCounter);
        entry.setLoadedTime(timeCounter);
        entry.setDirty(false); // la incarcare daca nu e dirty
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

    public int getTotalAccesses() {
        return timeCounter;
    }

    public ReplacementStrategy getReplacementStrategy() {
        return replacementStrategy;
    }

    public int getHitCount() {
        return timeCounter - pageFaultCount;
    }

    public double getHitRate() {
        if (timeCounter == 0) return 0.0;
        return (double) getHitCount() / timeCounter;
    }

    public double getMissRate() {
        if (timeCounter == 0) return 0.0;
        return (double) pageFaultCount / timeCounter;
    }

}
