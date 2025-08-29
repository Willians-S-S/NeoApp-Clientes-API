package br.com.neoapp.api.exceptions;

public class EmailExistsException extends RuntimeException {
    public static final String ERROR = "EMAIL_ALREADY_EXISTS";
    public EmailExistsException(String message) {
        super(message);
    }
}
