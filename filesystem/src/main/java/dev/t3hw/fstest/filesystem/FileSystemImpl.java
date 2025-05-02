package dev.t3hw.fstest.filesystem;

import java.time.Instant;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import dev.t3hw.fstest.common.avltree.AVLTreeMap;
import dev.t3hw.fstest.filesystem.exceptions.FileSystemExceptions;
import dev.t3hw.fstest.filesystem.fsobjects.Directory;
import dev.t3hw.fstest.filesystem.fsobjects.File;
import jakarta.annotation.PostConstruct;

@Component
public class FileSystemImpl implements FileSystem {
    NavigableMap<String, FileSystemNode> fileSystemMap = new AVLTreeMap<>();
    NavigableMap<Integer, Set<FileSystemNode>> filesBySize = new AVLTreeMap<>();

    @PostConstruct
    void init() {
        // Initialize the root directory
        Directory root = new Directory("/home", "home", Instant.now());
        fileSystemMap.put("/home", root);
    }
    
    @Override
    public File addFile(String parentDirName, String fileName, int size) {
        // find the parent directory
        FileSystemNode parentDir = fileSystemMap.get(parentDirName);
        if (parentDir == null || !(parentDir instanceof Directory)) {
            throw new FileSystemExceptions.FSNotFoundException("Parent directory not found or is not a directory");
        }

        // create the new file
        File newFile = new File(parentDirName, fileName, Instant.now(), size);
        fileSystemMap.put(parentDirName+"/"+fileName, newFile);
        filesBySize.computeIfAbsent(size, k -> ConcurrentHashMap.newKeySet()).add(newFile);

        return newFile;
    }
    
    @Override
    public Directory addDirectory(String parentDirName, String dirName) {
        // find the parent directory
        FileSystemNode parentDir = fileSystemMap.get(parentDirName);
        if (parentDir == null || !(parentDir instanceof Directory)) {
            throw new FileSystemExceptions.FSNotFoundException("Parent directory not found or is not a directory");
        }

        // create the new directory
        Directory newDir = new Directory(parentDirName, dirName, Instant.now());
        fileSystemMap.put(parentDirName+"/"+dirName, newDir);

        return newDir;
    }

    @Override
    public File getFile(String path) {
        // Find the file in the file system
        var file = fileSystemMap.get(path);
        if (file == null || !(file instanceof File)) {
            throw new FileSystemExceptions.FSNotFoundException("File not found or is not a file");
        }
        
        return (File) file;
    }

    @Override
    public Directory getDirectory(String path) {
        // Find the directory in the file system
        var dir = fileSystemMap.get(path);
        if (dir == null || !(dir instanceof Directory)) {
            throw new FileSystemExceptions.FSNotFoundException("Directory not found or is not a directory");
        }
        
        return (Directory) dir;
    }

    @Override
    public void delete(String path, boolean recursive) {
        // Find the file or directory in the file system
        var node = fileSystemMap.get(path);
        if (node == null) {
            throw new FileSystemExceptions.FSNotFoundException("File or directory not found");
        }

        // If it's a directory and recursive is true, delete all its contents
        if (node instanceof Directory) {
            // Get all files and directories in the directory
            var subNodes = fileSystemMap.subMap(path, false, fileSystemMap.floorKey(path+(char)127), true);

            if (!subNodes.values().isEmpty() && !recursive) {
                throw new FileSystemExceptions.DirectoryNotEmptyException("Directory is not empty");
            }
            
            subNodes.forEach((k,v) -> {
                fileSystemMap.remove(k);
                if (v instanceof File f) {
                    var subMap = filesBySize.subMap(f.getSize(), true, f.getSize(), true);
                    
                    var files = subMap.get(f.getSize());
                    if (files != null ) {
                        files.remove(f);
                    }
                    if (files == null || files.isEmpty()) {
                        filesBySize.remove(f.getSize());
                    }
                }
            });

        } else if (node instanceof File file) {
            var files = filesBySize.get(file.getSize());
            if (files != null) {
                files.remove(file);
            }
            if (files == null || files.isEmpty()) {
                filesBySize.remove(file.getSize());
            }
        }

        // Remove the file or directory from the file system
        fileSystemMap.remove(path);
    }

    @Override
    public  int getFileSize(String path) {
        // Find the file in the file system
        var file = fileSystemMap.get(path);
        if (file == null || !(file instanceof File)) {
            throw new FileSystemExceptions.FSNotFoundException("File not found or is not a file");
        }
        
        return ((File) file).getSize();
    }

    @Override
    public File getBiggestFile() {
        // Find the biggest file in the file system
        if (filesBySize.isEmpty()) {
            throw new FileSystemExceptions.FSNotFoundException("No files found");
        }
        
        var biggestFileEntry = filesBySize.lastEntry();
        return (File) biggestFileEntry.getValue().stream().findFirst().get();
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
        if (dir == null) {
            throw new FileSystemExceptions.FSNotFoundException("File Or Directory not found");
        }

        // Get all files in the directory
        var filesInDir = fileSystemMap.subMap(path, true, fileSystemMap.floorKey(path+(char)127), true);
        return filesInDir.values().stream().toList();
    }

    
}
