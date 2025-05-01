package dev.t3hw.fstest.exceptions;

public class CustomExceptions {
    public static class ParsingException extends RuntimeException {
        public ParsingException(String message) {
            super(message);
        }
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }
}
