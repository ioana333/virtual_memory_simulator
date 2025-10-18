package policy;

import java.util.LinkedHashMap;
import java.util.Map;

/** LRU pe cadre: victima este cadrul cel mai vechi accesat. */
public class LruPolicy implements ReplacementPolicy {
    // accessOrder=true => orice get/put mută intrarea la final (cea mai recentă)
    private final LinkedHashMap<Integer, Boolean> recency =
            new LinkedHashMap<>(16, 0.75f, true);

    @Override public void onLoad(int frameIndex, int pageId) {
        recency.put(frameIndex, Boolean.TRUE);
    }

    @Override public void onHit(int frameIndex, int pageId) {
        // accesăm ca să actualizăm ordinea
        recency.get(frameIndex);
    }

    @Override public int selectVictim() {
        for (Map.Entry<Integer, Boolean> e : recency.entrySet()) {
            return e.getKey(); // primul = cel mai vechi
        }
        throw new IllegalStateException("LRU: nu există cadre urmărite");
    }

    @Override public void onEvict(int frameIndex, int oldPageId) {
        // rămâne în map doar dacă va fi re-încărcat imediat; îl scoatem aici
        recency.remove(frameIndex);
    }
}
