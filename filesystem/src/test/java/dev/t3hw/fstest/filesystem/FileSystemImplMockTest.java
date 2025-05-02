package dev.t3hw.fstest.filesystem;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.t3hw.fstest.filesystem.exceptions.FileSystemExceptions;

@ExtendWith(MockitoExtension.class)
class FileSystemImplMockTest {

    @Spy
    private FileSystemImpl fileSystem;
    
    @Mock
    private NavigableMap<String, FileSystemNode> fileSystemMap;
    
    @Mock
    private NavigableMap<Long, FileSystemNode> filesBySize;
    
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
    void testAddFile() {
        // Setup
        String parentDirName = "/parent";
        String fileName = "file.txt";
        long size = 1024L;
        
        when(fileSystemMap.get(parentDirName)).thenReturn(mockDirectory);
        
        // Execute
        fileSystem.addFile(parentDirName, fileName, size);
        
        // Verify
        verify(fileSystemMap).get(parentDirName);
        verify(fileSystemMap).put(eq(parentDirName+"/"+fileName), any(File.class));
        verify(filesBySize).put(eq(size), any(File.class));
    }
    
    @Test
    void testAddFile_ParentNotFound() {
        // Setup
        String parentDirName = "/parent";
        String fileName = "/parent/file.txt";
        long size = 1024L;
        
        when(fileSystemMap.get(parentDirName)).thenReturn(null);
        
        // Execute and verify
        assertThrows(FileSystemExceptions.NotFoundException.class, 
            () -> fileSystem.addFile(parentDirName, fileName, size));
        
        verify(fileSystemMap).get(parentDirName);
        verify(fileSystemMap, never()).put(anyString(), any(FileSystemNode.class));
        verify(filesBySize, never()).put(anyLong(), any(FileSystemNode.class));
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
        assertThrows(FileSystemExceptions.NotFoundException.class, 
            () -> fileSystem.getFile(path));
        
        verify(fileSystemMap).get(path);
    }
    
    @Test
    void testGetFile_NotAFile() {
        // Setup
        String path = "/path/to/file.txt";
        
        when(fileSystemMap.get(path)).thenReturn(mockDirectory);
        
        // Execute and verify
        assertThrows(FileSystemExceptions.NotFoundException.class, 
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
        long expectedSize = 2048L;
        
        when(fileSystemMap.get(path)).thenReturn(mockFile);
        when(mockFile.getSize()).thenReturn(expectedSize);
        
        // Execute
        long size = fileSystem.getFileSize(path);
        
        // Verify
        assertEquals(expectedSize, size);
        verify(fileSystemMap).get(path);
        verify(mockFile).getSize();
    }
    
    @Test
    void testGetBiggestFile() {
        // Setup
        long biggestSize = 9999L;
        
        when(filesBySize.isEmpty()).thenReturn(false);
        when(filesBySize.lastEntry()).thenReturn(Map.entry(biggestSize, mockFile));
        
        // Execute
        File result = fileSystem.getBiggestFile();
        
        // Verify
        assertEquals(mockFile, result);
        verify(filesBySize).isEmpty();
        verify(filesBySize).lastEntry();
    }
    
    @Test
    void testGetBiggestFile_NoFiles() {
        // Setup
        when(filesBySize.isEmpty()).thenReturn(true);
        
        // Execute and verify
        assertThrows(FileSystemExceptions.NotFoundException.class, 
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
    
    @Test
    void testGetFilesInDirectory() {
        // Setup
        String path = "/path/to/dir";
        @SuppressWarnings("unchecked")
        NavigableMap<String, FileSystemNode> subMap = mock(NavigableMap.class);
        List<FileSystemNode> expectedNodes = List.of(mockDirectory, mockFile);
        
        when(fileSystemMap.get(path)).thenReturn(mockDirectory);
        when(fileSystemMap.subMap(eq(path), anyBoolean(), anyString(), anyBoolean())).thenReturn(subMap);
        when(subMap.values()).thenReturn(expectedNodes);
        
        // Execute
        List<FileSystemNode> result = fileSystem.getFilesInDirectory(path);
        
        // Verify
        assertEquals(expectedNodes, result);
        verify(fileSystemMap).get(path);
        verify(fileSystemMap).subMap(eq(path), anyBoolean(), anyString(), anyBoolean());
        verify(subMap).values();
    }
}