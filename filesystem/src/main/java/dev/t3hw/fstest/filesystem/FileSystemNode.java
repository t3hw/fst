package dev.t3hw.fstest.filesystem;

import java.time.Instant;

import lombok.AllArgsConstructor;

public interface FileSystemNode {
    String getName();
    Instant getCreationTime();
}

@AllArgsConstructor
class File implements FileSystemNode {
    private String name;
    private Instant creationTime;
    private long size;

    public String getName() { return name; }
    public long getSize() { return size; }
    public Instant getCreationTime() { return creationTime; }
}

@AllArgsConstructor
class Directory implements FileSystemNode {
    private String name;
    private Instant creationTime;

    public String getName() { return name; }
    public Instant getCreationTime() { return creationTime; }

}