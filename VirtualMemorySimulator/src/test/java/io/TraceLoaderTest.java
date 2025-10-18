package io;

import domain.PageRef;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class TraceLoaderTest {

    @Test
    void loadsSimpleTrace() throws Exception {
        Path path = resourcePath("traces/simple.txt");
        List<PageRef> refs = TraceLoader.load(path);
        assertFalse(refs.isEmpty());
        assertEquals(1, refs.get(0).pageId());
    }

    private Path resourcePath(String rel) throws URISyntaxException {
        var url = Objects.requireNonNull(
                getClass().getClassLoader().getResource(rel),
                "Missing test resource: " + rel
        );
        return Path.of(url.toURI());
    }
}
