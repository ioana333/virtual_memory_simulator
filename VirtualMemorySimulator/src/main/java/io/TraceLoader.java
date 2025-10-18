package io;

import domain.PageRef;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TraceLoader {
    // Format: "pageId" sau "pageId,R|W"; linii cu '#' sunt comentarii
    public static List<PageRef> load(Path file) throws IOException {
        List<PageRef> refs = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("[,;\\s]+");
                int page = Integer.parseInt(parts[0]);
                boolean write = parts.length > 1 && (parts[1].equalsIgnoreCase("W") || parts[1].equalsIgnoreCase("WRITE"));
                refs.add(new PageRef(page, write));
            }
        } catch (NumberFormatException e) {
            throw new IOException("Invalid number in trace file: " + file, e);
        }
        return refs;
    }
}
