package domain;

public class Frame {
    private int page = -1;        // -1 = liber
    private boolean valid = false;
    private boolean dirty = false;
    private long lastTouch = 0L;

    // --- getters (UI poate citi starea) ---
    public int getPage()       { return page; }
    public boolean isValid()   { return valid; }
    public boolean isDirty()   { return dirty; }
    public long getLastTouch() { return lastTouch; }

    // --- setteri (folosiți DOAR din core) ---
    public void setPage(int page)        { this.page = page; }
    public void setValid(boolean valid)  { this.valid = valid; }
    public void setDirty(boolean dirty)  { this.dirty = dirty; }
    public void touch(long tick)         { this.lastTouch = tick; }

    public void reset() {
        page = -1; valid = false; dirty = false; lastTouch = 0L;
    }
}
