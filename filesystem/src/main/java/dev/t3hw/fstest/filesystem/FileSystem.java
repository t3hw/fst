package dev.t3hw.fstest.filesystem;

import java.util.List;

import dev.t3hw.fstest.filesystem.fsobjects.Directory;
import dev.t3hw.fstest.filesystem.fsobjects.File;

public interface FileSystem {
    File addFile(String parentDirName, String fileName, int size);
    Directory addDirectory(String parentDirName, String dirName);
    File getFile(String path);
    Directory getDirectory(String path);
    void delete(String path, boolean recursive);
    int getFileSize(String path);
    File getBiggestFile();
    List<FileSystemNode> getAllFileSystem();
    List<FileSystemNode> getFilesInDirectory(String path);
}
