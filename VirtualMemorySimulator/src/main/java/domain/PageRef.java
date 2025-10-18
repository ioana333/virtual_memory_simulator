package domain;

public record PageRef(int pageId, boolean write) {
    public static PageRef read(int p) { return new PageRef(p, false); }
    public static PageRef write(int p) { return new PageRef(p, true); }
}
