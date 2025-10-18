package policy;

import core.MemoryCore;
import domain.PageRef;
import domain.SimStats;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FifoVsOptTest {

    private List<PageRef> seq(int... pages) {
        return java.util.Arrays.stream(pages).mapToObj(PageRef::read).collect(Collectors.toList());
    }

    @Test
    void fifoHasAtLeastOptFaults_onSmallTrace() {
        var trace = seq(1,2,3,2,4,1,2,5,2,1);
        var optList = trace.stream().map(PageRef::pageId).toList();

        var fifoCore = new MemoryCore(3, new FifoPolicy());
        var optCore  = new MemoryCore(3, new OptimalPolicy(optList));

        trace.forEach(fifoCore::step);
        trace.forEach(optCore::step);

        SimStats sF = fifoCore.stats();
        SimStats sO = optCore.stats();
        assertTrue(sF.pageFaults >= sO.pageFaults, "FIFO should not beat OPT on faults");
    }
}
