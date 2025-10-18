package policy;

import java.util.*;

public class OptimalPolicy implements ReplacementPolicy {
    private final List<Integer> future;               // întreaga urmă (doar pagini)
    private int cursor = 0;                           // poziția curentă în urmă
    private final Map<Integer,Integer> frameToPage = new HashMap<>();

    public OptimalPolicy(List<Integer> fullTrace) {
        this.future = Objects.requireNonNull(fullTrace);
    }

    @Override public void onLoad(int frame, int page) { frameToPage.put(frame, page); }
    @Override public void onHit(int frame, int page)  { /* n/a */ }
    @Override public void onEvict(int frame, int oldPage) { frameToPage.remove(frame); }

    // Se cheamă din core după fiecare acces (hit sau fault)
    public void advance() { cursor++; }

    @Override public int selectVictim() {
        int victimFrame = -1;
        int farthestUse = -1;
        for (var e : frameToPage.entrySet()) {
            int frame = e.getKey(), page = e.getValue();
            int next = nextUse(page);
            if (next == -1) return frame;          // nu se mai folosește -> ideal de scos
            if (next > farthestUse) { farthestUse = next; victimFrame = frame; }
        }
        return victimFrame;
    }

    private int nextUse(int page) {
        for (int i = cursor; i < future.size(); i++) if (future.get(i) == page) return i;
        return -1;
    }

    // util când (re)legăm pagina -> cadru la load
    public void bind(int frame, int page) { frameToPage.put(frame, page); }
}
