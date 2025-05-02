package dev.t3hw.fstest.filesystem;

import java.time.Instant;

public interface FileSystemNode {
    String getPath();
    String getName();
    Instant getCreationTime();
}
