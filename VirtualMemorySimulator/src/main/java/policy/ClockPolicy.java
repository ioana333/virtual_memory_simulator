package policy;

import java.util.Arrays;

/** Second-Chance (Clock): ține un bit de referință pentru fiecare cadru. */
public class ClockPolicy implements ReplacementPolicy {
    private final boolean[] inUse;
    private final boolean[] ref;
    private int hand = 0;

    public ClockPolicy(int frameCount) {
        this.inUse = new boolean[frameCount];
        this.ref   = new boolean[frameCount];
    }

    @Override public void onLoad(int frameIndex, int pageId) {
        inUse[frameIndex] = true;
        ref[frameIndex] = true; // give second chance inițial
    }

    @Override public void onHit(int frameIndex, int pageId) {
        ref[frameIndex] = true;
    }

    @Override public int selectVictim() {
        final int n = inUse.length;
        int spins = 0;
        while (true) {
            if (inUse[hand]) {
                if (!ref[hand]) {
                    int victim = hand;
                    hand = (hand + 1) % n;
                    return victim;
                }
                ref[hand] = false; // a doua șansă
            }
            hand = (hand + 1) % n;
            if (++spins > n * 4) throw new IllegalStateException("Clock: nu pot găsi victimă");
        }
    }

    @Override public void onEvict(int frameIndex, int oldPageId) {
        ref[frameIndex] = false; // se va seta la load
    }

    public void reset() {
        Arrays.fill(inUse, false);
        Arrays.fill(ref, false);
        hand = 0;
    }
}
