package ui;

import core.MemoryCore;
import domain.PageRef;
import io.TraceLoader;
import policy.FifoPolicy;
import policy.OptimalPolicy;

import java.nio.file.Path;
import java.util.List;

public class DemoMain {
    public static void main(String[] args) throws Exception {
        var refs = TraceLoader.load(Path.of("src/test/resources/traces/simple.txt"));
        var optList = refs.stream().map(PageRef::pageId).toList();

        var fifo = new MemoryCore(3, new FifoPolicy());
        var opt  = new MemoryCore(3, new OptimalPolicy(optList));

        refs.forEach(fifo::step);
        refs.forEach(opt::step);

        System.out.println("FIFO faults=" + fifo.stats().pageFaults);
        System.out.println("OPT  faults=" + opt.stats().pageFaults);
    }
}
