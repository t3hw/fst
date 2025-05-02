package dev.t3hw.fstest.filesystem.fsobjects;

import java.time.Instant;

import dev.t3hw.fstest.filesystem.FileSystemNode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class File implements FileSystemNode {
    private String path;
    private String name;
    private Instant creationTime;
    private int size;

    public String getPath() { return path; }
    public String getName() { return name; }
    public int getSize() { return size; }
    public Instant getCreationTime() { return creationTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof File)) return false;
        File file = (File) o;
        return size == file.size && path.equals(file.path) && name.equals(file.name);
    }
}
