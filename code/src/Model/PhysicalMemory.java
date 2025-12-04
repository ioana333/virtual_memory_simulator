package Model;

import java.util.ArrayList;
import java.util.List;

public class PhysicalMemory {
    private final List<Frame> frames;

    public PhysicalMemory(int numberOfFrames) {
        frames = new ArrayList<>();
        for (int i = 0; i < numberOfFrames; i++) {
            frames.add(new Frame(i));
        }
    }

    public Frame getFrame(int frameNumber) {
        return frames.get(frameNumber);
    }

    public List<Frame> getFrames() {
        return frames;
    }

    public Frame findFreeFrame() {
        for (Frame frame : frames) {
            if (frame.isFree()) {
                return frame;
            }
        }
        return null;
    }
}
