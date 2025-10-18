package core;

import domain.Frame;
import domain.PageRef;
import domain.SimStats;
import policy.OptimalPolicy;
import policy.ReplacementPolicy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

public class MemoryCore {
    private final Frame[] frames;
    private final ReplacementPolicy policy;
    private final SimStats stats = new SimStats();
    private long tick = 0;

    public MemoryCore(int frameCount, ReplacementPolicy policy) {
        if (frameCount <= 0) throw new IllegalArgumentException("frameCount must be > 0");
        this.frames = new Frame[frameCount];
        for (int i = 0; i < frameCount; i++) frames[i] = new Frame();
        this.policy = policy;
    }

    /** Read-only view pentru UI. */
    public List<Frame> getFrames() {
        return Collections.unmodifiableList(Arrays.asList(frames));
    }

    public SimStats stats() { return stats; }

    /** Returnează true pe HIT, false pe FAULT. */
    public boolean step(PageRef ref) {
        stats.accesses++;
        tick++;

        // HIT?
        OptionalInt hit = findFrame(ref.pageId());
        if (hit.isPresent()) {
            int f = hit.getAsInt();
            frames[f].touch(tick);
            if (ref.write()) frames[f].setDirty(true);
            policy.onHit(f, ref.pageId());
            if (policy instanceof OptimalPolicy opt) opt.advance();
            return true;
        }

        // PAGE FAULT
        stats.pageFaults++;

        int free = findFreeFrame();
        int use = (free != -1) ? free : policy.selectVictim();

        // Evicție dacă e cazul
        if (frames[use].isValid()) {
            if (frames[use].isDirty()) stats.writesBack++;
            policy.onEvict(use, frames[use].getPage());
        }

        // Load noua pagină
        frames[use].setPage(ref.pageId());
        frames[use].setValid(true);
        frames[use].setDirty(ref.write());
        frames[use].touch(tick);

        policy.onLoad(use, ref.pageId());
        if (policy instanceof OptimalPolicy opt) { opt.bind(use, ref.pageId()); opt.advance(); }

        return false;
    }

    private OptionalInt findFrame(int page) {
        for (int i = 0; i < frames.length; i++)
            if (frames[i].isValid() && frames[i].getPage() == page) return OptionalInt.of(i);
        return OptionalInt.empty();
    }

    private int findFreeFrame() {
        for (int i = 0; i < frames.length; i++)
            if (!frames[i].isValid()) return i;
        return -1;
    }
}
