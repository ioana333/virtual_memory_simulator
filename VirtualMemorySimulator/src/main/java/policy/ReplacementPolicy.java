package policy;

public interface ReplacementPolicy {
    void onLoad(int frameIndex, int pageId);
    void onHit(int frameIndex, int pageId);
    int  selectVictim();                 // chemat când nu e cadru liber
    void onEvict(int frameIndex, int oldPageId);
}
