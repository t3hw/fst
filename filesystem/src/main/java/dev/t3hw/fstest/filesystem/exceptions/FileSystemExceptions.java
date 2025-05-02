package dev.t3hw.fstest.filesystem.exceptions;

public class FileSystemExceptions {

    public static class FSNotFoundException extends RuntimeException {
        public FSNotFoundException(String message) {
            super(message);
        }
    }

    public static class DirectoryNotEmptyException extends RuntimeException {
        public DirectoryNotEmptyException(String string) {
            super(string);
        }
    }
    
}
