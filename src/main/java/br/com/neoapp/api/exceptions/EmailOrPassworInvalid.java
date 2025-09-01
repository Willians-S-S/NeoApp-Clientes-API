package br.com.neoapp.api.exceptions;

public class EmailOrPassworInvalid extends RuntimeException {
    public static final String ERROR = "INVALID_EMAIL_OR_PASSWORD";

    public EmailOrPassworInvalid(String message) {
        super(message);
    }
}
