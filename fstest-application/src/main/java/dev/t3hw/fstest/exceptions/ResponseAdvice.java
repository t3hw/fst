package dev.t3hw.fstest.exceptions;

import java.time.OffsetDateTime;

import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.ServletRequestPathUtils;

import dev.t3hw.fstest.model.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ResponseAdvice extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(CustomExceptions.ParsingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleParsingException(CustomExceptions.ParsingException e, WebRequest request) {
        return handleException(e, request, HttpStatus.BAD_REQUEST, Level.INFO);
    }

    @ExceptionHandler(CustomExceptions.NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleNotFoundException(CustomExceptions.NotFoundException e, WebRequest request) {
        return handleException(e, request, HttpStatus.NOT_FOUND, Level.TRACE);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDTO> handleUncaughtException(Exception e, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(handleException(e, request, HttpStatus.INTERNAL_SERVER_ERROR, Level.ERROR));
    }

    private ErrorResponseDTO handleException(Exception e, WebRequest request, HttpStatus status, Level logLevel) {
        switch (logLevel) {
            case Level.ERROR:
                log.error("Unexpected exception occured: {}", e.getMessage(), e);
                break;
            default:
                log.atLevel(logLevel)
                   .log(e.getMessage());
                break;
        }
        
        return new ErrorResponseDTO()
            .error(e.getMessage())
            .path(request.getAttribute(ServletRequestPathUtils.PATH_ATTRIBUTE, 0).toString())
            .timestamp(OffsetDateTime.now())
            .status(status.value())
            .error(status.getReasonPhrase())
            .exception(e.getClass().getSimpleName());
    }
}
