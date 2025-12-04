package Model;

public class Frame
{
    private int frameNumber;
    private Integer pageNumber;

    public Frame(int frameNumber)
    {
        this.frameNumber = frameNumber;
        this.pageNumber = null;
    }

    public boolean isFree()
    {
        return pageNumber == null;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }
}
