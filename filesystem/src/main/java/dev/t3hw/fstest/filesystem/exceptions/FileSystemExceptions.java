package dev.t3hw.fstest.filesystem.exceptions;

public class FileSystemExceptions {

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }

    public static class DirectoryNotEmptyException extends RuntimeException {
        public DirectoryNotEmptyException(String string) {
            super(string);
        }
    }
    
}
