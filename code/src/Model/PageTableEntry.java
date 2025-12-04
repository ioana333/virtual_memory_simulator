package Model;

import java.util.Date;

public class PageTableEntry
{
    private int pageNumber;
    private boolean inRAM;
    private int frameNumber;
    private boolean dirty;
    private int lastUsedTime;
    private int loadedTime;

    public PageTableEntry(int pageNumber)
    {
        this.pageNumber = pageNumber;
        this.inRAM = false;
        this.frameNumber = -1;
        this.dirty = false;
        this.lastUsedTime = -1;
        this.loadedTime = -1;
    }

    public void setInRAM(boolean inRAM) {
        this.inRAM = inRAM;
    }

    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void setLastUsedTime(int lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }

    public void setLoadedTime(int loadedTime) {
        this.loadedTime = loadedTime;
    }

    public boolean isInRAM() {
        return inRAM;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public boolean isDirty() {
        return dirty;
    }

    public int getLastUsedTime() {
        return lastUsedTime;
    }

    public int getLoadedTime() {
        return loadedTime;
    }

    public int getPageNumber() {
        return pageNumber;
    }
}
