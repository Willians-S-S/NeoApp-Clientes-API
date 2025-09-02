package br.com.neoapp.api.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleValidationErrors(MethodArgumentNotValidException e, HttpServletRequest request){
        int status = HttpStatus.UNPROCESSABLE_ENTITY.value();

        ValidationError err = (ValidationError) ValidationError.builder()
                .timestamp(Instant.now())
                .status(status)
                .error("Validation Error")
                .message("Dados inválidos. Verifique os erros de cada campo.")
                .path(request.getRequestURI())
                .build();

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            err.addError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ClientNotFound.class)
    public ResponseEntity<StandardError> handleClientNotFound(ClientNotFound e, HttpServletRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity
                .status(status)
                .body(StandardError
                        .builder()
                        .timestamp(Instant.now())
                        .status(status.value())
                        .error(ClientNotFound.ERROR)
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .build()
                );
    }

    @ExceptionHandler(EmailOrPassworInvalid.class)
    public ResponseEntity<StandardError> handleEmailOrPassInvalid(EmailOrPassworInvalid e, HttpServletRequest request){
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return ResponseEntity
                .status(status)
                .body(StandardError
                        .builder()
                        .timestamp(Instant.now())
                        .status(status.value())
                        .error(EmailOrPassworInvalid.ERROR)
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .build()
                );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<StandardError> handleRuntimeException(RuntimeException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(status)
                .body(StandardError
                        .builder()
                        .timestamp(Instant.now())
                        .status(status.value())
                        .error("INTERNAL_ERROR")
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .build()
                );
    }
}
