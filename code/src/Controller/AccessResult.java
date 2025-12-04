package Controller;

public class AccessResult
{
    private final int pageNumber;
    private final boolean hit;
    private final Integer victimPage;
    private final int pageFaultCount;
    private final int time;

    public AccessResult(int pageNumber, boolean hit, Integer victimPage,
                        int pageFaultCount, int time) {
        this.pageNumber = pageNumber;
        this.hit = hit;
        this.victimPage = victimPage;
        this.pageFaultCount = pageFaultCount;
        this.time = time;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public boolean isHit() {
        return hit;
    }

    public Integer getVictimPage() {
        return victimPage;
    }

    public int getPageFaultCount() {
        return pageFaultCount;
    }

    public int getTime() {
        return time;
    }
}
