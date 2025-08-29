package br.com.neoapp.api.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<StandardError> emailExists(EmailExistsException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.CONFLICT;
        return ResponseEntity
                .status(status)
                .body(StandardError
                        .builder()
                        .timestamp(Instant.now())
                        .status(status.value())
                        .error(EmailExistsException.ERROR)
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .build()
                );
    }

    @ExceptionHandler(CpfExistsException.class)
    public ResponseEntity<StandardError> cpfExists(CpfExistsException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.CONFLICT;
        return ResponseEntity
                .status(status)
                .body(StandardError
                        .builder()
                        .timestamp(Instant.now())
                        .status(status.value())
                        .error(CpfExistsException.ERROR)
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .build()
                );
    }
}
