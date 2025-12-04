package Controller;
import Replacement.*;

public class Simulator {

    public static void main(String[] args) {
        int numberOfPages = 8;
        int numberOfFrames = 3;

        ReplacementStrategy strategy = new FifoReplacementStrategy();
        MemoryManager mm = new MemoryManager(numberOfPages, numberOfFrames, strategy);

        int[] accessSequence = {0, 2, 1, 0, 3, 2, 4, 0, 2, 3};

        for (int page : accessSequence) {
            AccessResult result = mm.accessPage(page);

            System.out.println("Access page: " + page +
                    " | hit = " + result.isHit() +
                    " | victim = " + result.getVictimPage() +
                    " | pageFaults = " + result.getPageFaultCount());
        }
    }
}
