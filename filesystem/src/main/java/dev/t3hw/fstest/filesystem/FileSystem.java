package dev.t3hw.fstest.filesystem;

import java.util.List;

public interface FileSystem {
    void addFile(String parentDirName, String fileName, long size);
    void addDirectory(String parentDirName, String dirName);
    FileSystemNode getFile(String path);
    FileSystemNode getDirectory(String path);
    void delete(String path, boolean recursive);
    long getFileSize(String path);
    File getBiggestFile();
    List<FileSystemNode> getAllFileSystem();
    List<FileSystemNode> getFilesInDirectory(String path);
}
