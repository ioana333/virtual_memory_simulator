package domain;

public class SimStats {
    public long accesses;
    public long pageFaults;
    public long writesBack;

    public double faultRate() {
        return accesses == 0 ? 0.0 : (double) pageFaults / accesses;
    }
}
