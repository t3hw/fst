package dev.t3hw.fstest.filesystem;

import java.time.Instant;
import java.util.List;
import java.util.NavigableMap;

import org.springframework.stereotype.Component;

import dev.t3hw.fstest.common.avltree.AVLTreeMap;
import dev.t3hw.fstest.common.avltree.AVLTreeMap.OverrideStrategy;
import dev.t3hw.fstest.filesystem.exceptions.FileSystemExceptions;
import jakarta.annotation.PostConstruct;

@Component
public class FileSystemImpl implements FileSystem {
    NavigableMap<String, FileSystemNode> fileSystemMap = new AVLTreeMap<>();
    NavigableMap<Long, FileSystemNode> filesBySize = new AVLTreeMap<>(1, OverrideStrategy.ADDITIVITY);

    @PostConstruct
    void init() {
        // Initialize the root directory
        Directory root = new Directory("/home", Instant.now());
        fileSystemMap.put("/home", root);
    }
    
    @Override
    public void addFile(String parentDirName, String fileName, long size) {
        // find the parent directory
        FileSystemNode parentDir = fileSystemMap.get(parentDirName);
        if (parentDir == null || !(parentDir instanceof Directory)) {
            throw new FileSystemExceptions.NotFoundException("Parent directory not found or is not a directory");
        }

        // create the new file
        File newFile = new File(fileName, Instant.now(), size);
        fileSystemMap.put(parentDirName+"/"+fileName, newFile);
        filesBySize.put(size, newFile);
    }
    
    @Override
    public void addDirectory(String parentDirName, String dirName) {
        // find the parent directory
        FileSystemNode parentDir = fileSystemMap.get(parentDirName);
        if (parentDir == null || !(parentDir instanceof Directory)) {
            throw new FileSystemExceptions.NotFoundException("Parent directory not found or is not a directory");
        }

        // create the new directory
        Directory newDir = new Directory(dirName, Instant.now());
        fileSystemMap.put(parentDirName+"/"+dirName, newDir);
    }

    @Override
    public File getFile(String path) {
        // Find the file in the file system
        var file = fileSystemMap.get(path);
        if (file == null || !(file instanceof File)) {
            throw new FileSystemExceptions.NotFoundException("File not found or is not a file");
        }
        
        return (File) file;
    }

    @Override
    public Directory getDirectory(String path) {
        // Find the directory in the file system
        var dir = fileSystemMap.get(path);
        if (dir == null || !(dir instanceof Directory)) {
            throw new FileSystemExceptions.NotFoundException("Directory not found or is not a directory");
        }
        
        return (Directory) dir;
    }

    @Override
    public void delete(String path, boolean recursive) {
        // Find the file or directory in the file system
        var node = fileSystemMap.get(path);
        if (node == null) {
            throw new FileSystemExceptions.NotFoundException("File or directory not found");
        }

        // If it's a directory and recursive is true, delete all its contents
        if (node instanceof Directory && recursive) {
            // Get all files and directories in the directory
            var subNodes = fileSystemMap.subMap(path, false, fileSystemMap.floorKey(path+(char)127), true);
            
            subNodes.forEach((k,v) -> {
                fileSystemMap.remove(k);
                if (v instanceof File f) {
                    filesBySize.subMap(f.getSize(), true, f.getSize(), true).remove(f.getSize(), f);
                }
            });

        }

        // If it's a directory and recursive is false, throw an exception
        if (node instanceof Directory && !recursive) {
            throw new FileSystemExceptions.DirectoryNotEmptyException("Directory is not empty");
        }

        // Remove the file or directory from the file system
        fileSystemMap.remove(path);
    }

    @Override
    public long getFileSize(String path) {
        // Find the file in the file system
        var file = fileSystemMap.get(path);
        if (file == null || !(file instanceof File)) {
            throw new FileSystemExceptions.NotFoundException("File not found or is not a file");
        }
        
        return ((File) file).getSize();
    }

    @Override
    public File getBiggestFile() {
        // Find the biggest file in the file system
        if (filesBySize.isEmpty()) {
            throw new FileSystemExceptions.NotFoundException("No files found");
        }
        
        var biggestFileEntry = filesBySize.lastEntry();
        return (File) biggestFileEntry.getValue();
    }

    @Override
    public List<FileSystemNode> getAllFileSystem() {
        var allFiles = fileSystemMap.values();
        return allFiles.stream().toList();
    }

    @Override
    public List<FileSystemNode> getFilesInDirectory(String path) {
        // Find the directory in the file system
        var dir = fileSystemMap.get(path);
        if (dir == null || !(dir instanceof Directory)) {
            throw new FileSystemExceptions.NotFoundException("Directory not found or is not a directory");
        }

        // Get all files in the directory
        var filesInDir = fileSystemMap.subMap(path, true, path + "/\uFFFF", true);
        return filesInDir.values().stream().toList();
    }

    
}
