package dev.t3hw.fstest.filesystem;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableMap;

import dev.t3hw.fstest.common.avltree.AVLTreeMap;

public interface FileSystemNode {
    String getName();
    long getSize();
    Instant getCreationTime();
    boolean isDirectory();

    // Static comparators are convenient
    Comparator<FileSystemNode> NAME_COMPARATOR = Comparator.comparing(FileSystemNode::getName);
    Comparator<FileSystemNode> SIZE_COMPARATOR = Comparator.comparingLong(FileSystemNode::getSize);
    Comparator<FileSystemNode> CREATION_TIME_COMPARATOR = Comparator.comparing(FileSystemNode::getCreationTime);
    Comparator<FileSystemNode> TYPE_COMPARATOR = Comparator.comparing(FileSystemNode::isDirectory); // Directories first/last?
}

class FileNode implements FileSystemNode {
    private String name;
    private Instant creationTime;
    private long size;

    public FileNode(String name, long size) {
        this.name = name;
        this.size = size;
        this.creationTime = Instant.now();
    }

    @Override public String getName() { return name; }
    @Override public long getSize() { return size; }
    @Override public Instant getCreationTime() { return creationTime; }
    @Override public boolean isDirectory() { return false; }
}

class DirectoryNode implements FileSystemNode {
    private String name;
    private Instant creationTime;
    private NavigableMap<String, FileSystemNode> children = new AVLTreeMap<String, FileSystemNode>(); 

    public DirectoryNode(String name) {
        this.name = name;
        this.creationTime = Instant.now();
    }

    public void addChild(FileSystemNode node) {
        children.put(node.getName(), node);
    }

    public FileSystemNode getChild(String name) {
        return children.get(name);
    }

    // Method to get children sorted by name (default TreeMap order)
    public List<FileSystemNode> getChildrenSortedByName() {
        return new ArrayList<>(children.values()); // TreeMap values are iterated in key order
    }

    // *** NEW METHOD for custom sorting ***
    public List<FileSystemNode> getChildrenSortedBy(Comparator<FileSystemNode> comparator) {
        List<FileSystemNode> list = new ArrayList<>(children.values());
        list.sort(comparator);
        return list;
    }
    
    // Optional: Combine comparators, e.g., sort by type then name
    public List<FileSystemNode> getChildrenSortedByTypeThenName() {
         List<FileSystemNode> list = new ArrayList<>(children.values());
         // Sort first by type (e.g., directories first), then by name within types
         list.sort(FileSystemNode.TYPE_COMPARATOR.thenComparing(FileSystemNode.NAME_COMPARATOR));
         return list;
    }


    @Override public String getName() { return name; }
    @Override public Instant getCreationTime() { return creationTime; }
    @Override public boolean isDirectory() { return true; }
    
    // Size calculation for directory might be recursive or just 0/fixed
    @Override public long getSize() { 
        // Option 1: Fixed size for directory entry itself
        // return 4096; 
        // Option 2: Calculate recursively (can be slow for deep trees)
         return children.values().stream().mapToLong(FileSystemNode::getSize).sum();
    }
    // ... other methods
}