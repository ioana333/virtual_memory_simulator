package policy;

import java.util.ArrayDeque;

public class FifoPolicy implements ReplacementPolicy {
    private final ArrayDeque<Integer> q = new ArrayDeque<>();

    @Override public void onLoad(int frameIndex, int pageId) { q.addLast(frameIndex); }
    @Override public void onHit(int frameIndex, int pageId) { /* nimic */ }
    @Override public int  selectVictim() { return q.removeFirst(); }
    @Override public void onEvict(int frameIndex, int oldPageId) { /* nimic */ }
}
