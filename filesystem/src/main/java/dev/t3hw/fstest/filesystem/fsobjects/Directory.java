package dev.t3hw.fstest.filesystem.fsobjects;

import java.time.Instant;

import dev.t3hw.fstest.filesystem.FileSystemNode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Directory implements FileSystemNode {
    private String path;
    private String name;
    private Instant creationTime;

    public String getPath() { return path; }
    public String getName() { return name; }
    public Instant getCreationTime() { return creationTime; }

}