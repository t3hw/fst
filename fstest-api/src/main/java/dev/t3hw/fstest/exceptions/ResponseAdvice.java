package dev.t3hw.fstest.exceptions;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.event.Level;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import dev.t3hw.fstest.common.avltree.AVLTreeMap.NodeAlreadyExistsException;
import dev.t3hw.fstest.filesystem.exceptions.FileSystemExceptions.DirectoryNotEmptyException;
import dev.t3hw.fstest.filesystem.exceptions.FileSystemExceptions.FSNotFoundException;
import dev.t3hw.fstest.model.ProblemDetails;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ResponseAdvice extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler({
        FSNotFoundException.class
    })
    public ResponseEntity<Object> handleNotFoundException(Exception e, WebRequest request) {
        return getProblemDetailsAndLog(e, null, HttpStatus.NOT_FOUND, request, Level.WARN);
    }

    @ExceptionHandler({
        DirectoryNotEmptyException.class,
        NodeAlreadyExistsException.class,
    })
    public ResponseEntity<Object> handleDirectoryNotEmptyException(Exception e, WebRequest request) {
        return getProblemDetailsAndLog(e, null, HttpStatus.CONFLICT, request, Level.WARN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUncaughtException(Exception e, WebRequest request) {
        return getProblemDetailsAndLog(e, null, HttpStatus.INTERNAL_SERVER_ERROR, request, Level.ERROR);
    }
    
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        return getProblemDetailsAndLog(ex, headers, status, request, Level.DEBUG);
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
        HandlerMethodValidationException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        return getProblemDetailsAndLog(ex, headers, status, request, Level.DEBUG);
    }

    private ResponseEntity<Object> getProblemDetailsAndLog(
        Exception ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request,
        Level logLevel
    ) {
        Map<String, Object> properties = new HashMap<>();

        switch (logLevel) {
            case Level.ERROR:
                log.error("Unexpected exception occured: {}", ex.getMessage(), ex);
                properties = Map.of("exception", ex.getClass().getSimpleName());
                break;
            default:
                log.atLevel(logLevel)
                   .log(ex.getMessage());
                break;
        }

        if (ex instanceof MethodValidationResult validationError) {
            properties.put("validationErrors", validationError.getAllErrors());
        } else if (ex instanceof BindException bindException) {
            properties.put("bindErrors", bindException.getAllErrors());
        }

        if (properties.size() == 0) {
            properties = null;
        }

        final String title;
        if (status instanceof HttpStatus st) {
            title = st.getReasonPhrase();
        } else {
            title = status.toString();
        }

        ProblemDetails problemDetails = new ProblemDetails()
                                                .title(title)
                                                .status(status.value())
                                                .detail(ex.getMessage())
                                                .instance(URI.create(request.getDescription(false)))
                                                .properties(properties);

        problemDetails.setProperties(properties);

        return ResponseEntity.status(problemDetails.getStatus())
                             .headers(headers)
                             .body(problemDetails);
    }

}
