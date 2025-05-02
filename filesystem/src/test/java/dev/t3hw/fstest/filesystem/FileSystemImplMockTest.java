package dev.t3hw.fstest.filesystem;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.NavigableMap;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.t3hw.fstest.filesystem.exceptions.FileSystemExceptions;
import dev.t3hw.fstest.filesystem.fsobjects.Directory;
import dev.t3hw.fstest.filesystem.fsobjects.File;

@ExtendWith(MockitoExtension.class)
class FileSystemImplMockTest {

    @Spy
    private FileSystemImpl fileSystem;
    
    @Mock
    private NavigableMap<String, FileSystemNode> fileSystemMap;
    
    @Mock
    private NavigableMap<Integer, Set<FileSystemNode>> filesBySize;
    
    @Mock
    private Directory mockDirectory;
    
    @Mock
    private File mockFile;
    
    @BeforeEach
    void setUp() {
        fileSystem.fileSystemMap = fileSystemMap;
        fileSystem.filesBySize = filesBySize;
    }
    
    @Test
    void testAddDirectory() {
        // Setup
        String parentDirName = "/parent";
        String dirName = "dir";
        
        when(fileSystemMap.get(parentDirName)).thenReturn(mockDirectory);
        
        // Execute
        fileSystem.addDirectory(parentDirName, dirName);
        
        // Verify
        verify(fileSystemMap).get(parentDirName);
        verify(fileSystemMap).put(eq(parentDirName+"/"+dirName), any(Directory.class));
    }
    
    @Test
    void testGetFile() {
        // Setup
        String path = "/path/to/file.txt";
        
        when(fileSystemMap.get(path)).thenReturn(mockFile);
        
        // Execute
        File result = fileSystem.getFile(path);
        
        // Verify
        assertEquals(mockFile, result);
        verify(fileSystemMap).get(path);
    }
    
    @Test
    void testGetFile_NotFound() {
        // Setup
        String path = "/path/to/file.txt";
        
        when(fileSystemMap.get(path)).thenReturn(null);
        
        // Execute and verify
        assertThrows(FileSystemExceptions.FSNotFoundException.class, 
            () -> fileSystem.getFile(path));
        
        verify(fileSystemMap).get(path);
    }
    
    @Test
    void testGetFile_NotAFile() {
        // Setup
        String path = "/path/to/file.txt";
        
        when(fileSystemMap.get(path)).thenReturn(mockDirectory);
        
        // Execute and verify
        assertThrows(FileSystemExceptions.FSNotFoundException.class, 
            () -> fileSystem.getFile(path));
        
        verify(fileSystemMap).get(path);
    }
    
    @Test
    void testGetDirectory() {
        // Setup
        String path = "/path/to/dir";
        
        when(fileSystemMap.get(path)).thenReturn(mockDirectory);
        
        // Execute
        Directory result = fileSystem.getDirectory(path);
        
        // Verify
        assertEquals(mockDirectory, result);
        verify(fileSystemMap).get(path);
    }    
    
    @Test
    void testGetFileSize() {
        // Setup
        String path = "/path/to/file.txt";
        int expectedSize = 2048;
        
        when(fileSystemMap.get(path)).thenReturn(mockFile);
        when(mockFile.getSize()).thenReturn(expectedSize);
        
        // Execute
        int size = fileSystem.getFileSize(path);
        
        // Verify
        assertEquals(expectedSize, size);
        verify(fileSystemMap).get(path);
        verify(mockFile).getSize();
    }
    
    @Test
    void testGetBiggestFile_NoFiles() {
        // Setup
        when(filesBySize.isEmpty()).thenReturn(true);
        
        // Execute and verify
        assertThrows(FileSystemExceptions.FSNotFoundException.class, 
            () -> fileSystem.getBiggestFile());
        
        verify(filesBySize).isEmpty();
        verify(filesBySize, never()).lastEntry();
    }
    
    @Test
    void testGetAllFileSystem() {
        // Setup
        List<FileSystemNode> expectedNodes = List.of(mockDirectory, mockFile);
        
        when(fileSystemMap.values()).thenReturn(expectedNodes);
        
        // Execute
        List<FileSystemNode> result = fileSystem.getAllFileSystem();
        
        // Verify
        assertEquals(expectedNodes, result);
        verify(fileSystemMap).values();
    }
}