package dev.t3hw.fstest.filesystem;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.t3hw.fstest.common.avltree.AVLTreeMap;
import dev.t3hw.fstest.filesystem.exceptions.FileSystemExceptions;
import dev.t3hw.fstest.filesystem.exceptions.FileSystemExceptions.DirectoryNotEmptyException;
import dev.t3hw.fstest.filesystem.fsobjects.Directory;
import dev.t3hw.fstest.filesystem.fsobjects.File;

@ExtendWith(MockitoExtension.class)
class FileSystemImplTest {

    @InjectMocks
    private FileSystemImpl fileSystem;
    
    @Mock
    private AVLTreeMap<String, FileSystemNode> fileSystemMap;
    
    @Mock
    private AVLTreeMap<Integer, FileSystemNode> filesBySize;
    
    @BeforeEach
    void setUp() {
        fileSystem = new FileSystemImpl();
        fileSystem.init(); // Call the init method to create the root directory
    }
    
    @Test
    void testInit() {
        // Verify root directory is created
        assertNotNull(fileSystem.fileSystemMap.get("/home"));
        assertTrue(fileSystem.fileSystemMap.get("/home") instanceof Directory);
    }
    
    @Test
    void testAddFile() {
        // Test adding a file to existing directory
        // Setup
        String parentDir = "/home";
        String fileName = "test.txt";
        int fileSize = 1024;
        
        // Execute
        fileSystem.addFile(parentDir, fileName, fileSize);
        
        // Verify
        FileSystemNode node = fileSystem.fileSystemMap.get(parentDir+"/"+fileName);
        assertNotNull(node);
        assertTrue(node instanceof File);
        assertEquals(fileSize, ((File) node).getSize());
        assertTrue(fileSystem.filesBySize.get(fileSize).contains(node));
    }
    
    @Test
    void testAddFile_ParentDirectoryNotFound() {
        // Test adding a file to non-existent directory
        String nonExistentDir = "/nonexistent";
        String fileName = "/nonexistent/test.txt";
        int fileSize = 1024;
        
        // Execute and verify exception
        FileSystemExceptions.FSNotFoundException exception = assertThrows(
            FileSystemExceptions.FSNotFoundException.class,
            () -> fileSystem.addFile(nonExistentDir, fileName, fileSize)
        );
        
        assertEquals("Parent directory not found or is not a directory", exception.getMessage());
    }
    
    @Test
    void testAddDirectory() {
        // Test adding a directory to existing directory
        String parentDir = "/home";
        String dirName = "testdir";
        
        // Execute
        fileSystem.addDirectory(parentDir, dirName);
        
        // Verify
        FileSystemNode node = fileSystem.fileSystemMap.get(parentDir+"/"+dirName);
        assertNotNull(node);
        assertTrue(node instanceof Directory);
    }
    
    @Test
    void testAddDirectory_ParentDirectoryNotFound() {
        // Test adding a directory to non-existent directory
        String nonExistentDir = "/nonexistent";
        String dirName = "/nonexistent/testdir";
        
        // Execute and verify exception
        FileSystemExceptions.FSNotFoundException exception = assertThrows(
            FileSystemExceptions.FSNotFoundException.class,
            () -> fileSystem.addDirectory(nonExistentDir, dirName)
        );
        
        assertEquals("Parent directory not found or is not a directory", exception.getMessage());
    }
    
    @Test
    void testGetFile() {
        // Setup
        String filePath = "test.txt";
        int fileSize = 1024;
        fileSystem.addFile("/home", filePath, fileSize);
        
        // Execute
        File file = fileSystem.getFile("/home/"+filePath);
        
        // Verify
        assertNotNull(file);
        assertEquals(fileSize, file.getSize());
    }
    
    @Test
    void testGetFile_NotFound() {
        // Test getting a non-existent file
        String nonExistentFile = "/home/nonexistent.txt";
        
        // Execute and verify exception
        FileSystemExceptions.FSNotFoundException exception = assertThrows(
            FileSystemExceptions.FSNotFoundException.class,
            () -> fileSystem.getFile(nonExistentFile)
        );
        
        assertEquals("File not found or is not a file", exception.getMessage());
    }
    
    @Test
    void testGetDirectory() {
        // Setup
        String dirPath = "/home/testdir";
        fileSystem.addDirectory("/home", dirPath);
        
        // Execute
        Directory dir = fileSystem.getDirectory("/home/"+dirPath);
        
        // Verify
        assertNotNull(dir);
    }
    
    @Test
    void testGetDirectory_NotFound() {
        // Test getting a non-existent directory
        String nonExistentDir = "/home/nonexistent";
        
        // Execute and verify exception
        FileSystemExceptions.FSNotFoundException exception = assertThrows(
            FileSystemExceptions.FSNotFoundException.class,
            () -> fileSystem.getDirectory(nonExistentDir)
        );
        
        assertEquals("Directory not found or is not a directory", exception.getMessage());
    }
    
    @Test
    void testDelete_File() {
        // Setup
        String filePath = "test.txt";
        fileSystem.addFile("/home", filePath, 1024);
        
        // Execute
        fileSystem.delete("/home/"+filePath, false);
        
        // Verify
        assertNull(fileSystem.fileSystemMap.get(filePath));
    }
    
    @Test
    void testDelete_EmptyDirectory() {
        // Setup
        String dirPath = "testdir";
        fileSystem.addDirectory("/home", dirPath);
        fileSystem.addDirectory("/home/testdir", dirPath);

        
        // Execute
        assertThrows(DirectoryNotEmptyException.class, () -> fileSystem.delete("/home/" + dirPath, false));
        
        // Verify
        assertNull(fileSystem.fileSystemMap.get(dirPath));
    }
    
    @Test
    void testDelete_NonEmptyDirectory_WithoutRecursive() {
        // Setup
        String dirPath = "testdir";
        String filePath = "test.txt";
        fileSystem.addDirectory("/home", dirPath);
        fileSystem.addFile("/home/testdir", filePath, 1024);
        
        // Execute and verify exception
        FileSystemExceptions.DirectoryNotEmptyException exception = assertThrows(
            FileSystemExceptions.DirectoryNotEmptyException.class,
            () -> fileSystem.delete("/home/testdir", false)
        );
        
        assertEquals("Directory is not empty", exception.getMessage());
    }
    
    @Test
    void testDelete_NonEmptyDirectory_WithRecursive() {
        // Setup
        String dirPath = "/home/testdir";
        String filePath = "/home/testdir/test.txt";
        fileSystem.addDirectory("/home", "testdir");
        fileSystem.addFile("/home/testdir", "test.txt", 1024);
        
        // Execute
        fileSystem.delete(dirPath, true);
        
        // Verify
        assertNull(fileSystem.fileSystemMap.get(dirPath));
        assertNull(fileSystem.fileSystemMap.get(filePath));
    }

    @Test
    void testDelete_NonEmptyDirectory_WithRecursiveMulti() {
        // Setup
        String dirPath = "/home/testdir";
        String filePath = "/home/testdir/test.txt";
        fileSystem.addDirectory("/home", "testdir");
        fileSystem.addFile("/home/testdir", "test.txt", 1024);
        
        // Adding another file to the directory
        String anotherFilePath = "/home/testdir/anotherTest.txt";
        fileSystem.addFile("/home/testdir", "anotherTest.txt", 2048);

        // Verify both files are present before deletion
        assertNotNull(fileSystem.fileSystemMap.get(filePath));
        assertNotNull(fileSystem.fileSystemMap.get(anotherFilePath));

        // Execute
        fileSystem.delete(dirPath, true);
        
        // Verify
        assertNull(fileSystem.fileSystemMap.get(dirPath));
        assertNull(fileSystem.fileSystemMap.get(filePath));
    }

    @Test
    void testMultiFileSameSize() {
        // Setup
        String filePath1 = "test1.txt";
        String filePath2 = "test2.txt";
        int fileSize = 1024;
        fileSystem.addFile("/home", filePath1, fileSize);
        fileSystem.addFile("/home", filePath2, fileSize);
        
        // Execute
        File biggestFile = fileSystem.getBiggestFile();
        
        // Verify
        assertNotNull(biggestFile);
        assertEquals(fileSize, biggestFile.getSize());
    }
    
    @Test
    void testDeleteMultiFileSameSize() {
        // Setup
        String filePath1 = "test1.txt";
        String filePath2 = "test2.txt";
        int fileSize = 1024;
        fileSystem.addFile("/home", filePath1, fileSize);
        fileSystem.addFile("/home", filePath2, fileSize);
        
        // Execute
        fileSystem.delete("/home/"+filePath2, false);
        
        // Verify
        assertNull(fileSystem.fileSystemMap.get(filePath2));
    }

    @Test
    void testGetFileSize() {
        // Setup
        String filePath = "test.txt";
        int fileSize = 2048;
        fileSystem.addFile("/home", filePath, fileSize);
        
        // Execute
        int size = fileSystem.getFileSize("/home/"+filePath);
        
        // Verify
        assertEquals(fileSize, size);
    }
    
    @Test
    void testGetFileSize_FileNotFound() {
        // Test getting size of non-existent file
        String nonExistentFile = "/home/nonexistent.txt";
        
        // Execute and verify exception
        FileSystemExceptions.FSNotFoundException exception = assertThrows(
            FileSystemExceptions.FSNotFoundException.class,
            () -> fileSystem.getFileSize(nonExistentFile)
        );
        
        assertEquals("File not found or is not a file", exception.getMessage());
    }
    
    @Test
    void testGetBiggestFile() {
        // Setup
        fileSystem.addFile("/home", "/home/small.txt", 512);
        fileSystem.addFile("/home", "/home/medium.txt", 1024);
        fileSystem.addFile("/home", "/home/large.txt", 2048);
        
        // Execute
        File biggestFile = fileSystem.getBiggestFile();
        
        // Verify
        assertNotNull(biggestFile);
        assertEquals(2048, biggestFile.getSize());
    }
    
    @Test
    void testGetBiggestFile_NoFiles() {
        // Setup - clear any files by re-initializing
        fileSystem = new FileSystemImpl();
        fileSystem.init();
        
        // Execute and verify exception
        FileSystemExceptions.FSNotFoundException exception = assertThrows(
            FileSystemExceptions.FSNotFoundException.class,
            () -> fileSystem.getBiggestFile()
        );
        
        assertEquals("No files found", exception.getMessage());
    }
    
    @Test
    void testGetAllFileSystem() {
        // Setup
        fileSystem.addDirectory("/home", "/home/dir1");
        fileSystem.addFile("/home", "/home/file1.txt", 1024);
        
        // Execute
        List<FileSystemNode> allNodes = fileSystem.getAllFileSystem();
        
        // Verify
        assertFalse(allNodes.isEmpty());
        assertEquals(3, allNodes.size()); // root + dir1 + file1.txt
    }
    
    @Test
    void testGetFilesInDirectory() {
        // Setup
        fileSystem.addDirectory("/home", "/home/dir1");
        fileSystem.addFile("/home", "/home/file1.txt", 1024);
        fileSystem.addFile("/home", "/home/file2.txt", 2048);
        
        // Execute
        List<FileSystemNode> filesInDir = fileSystem.getFilesInDirectory("/home");
        
        // Verify
        assertFalse(filesInDir.isEmpty());
        assertEquals(4, filesInDir.size()); // /home + dir1 + file1.txt + file2.txt
    }
    
    @Test
    void testGetFilesInDirectory_DirectoryNotFound() {
        // Test getting files in non-existent directory
        String nonExistentDir = "/nonexistent";
        
        // Execute and verify exception
        FileSystemExceptions.FSNotFoundException exception = assertThrows(
            FileSystemExceptions.FSNotFoundException.class,
            () -> fileSystem.getFilesInDirectory(nonExistentDir)
        );
        
        assertEquals("File Or Directory not found", exception.getMessage());
    }
}