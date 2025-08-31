package br.com.neoapp.api.exceptions;

public class ClientNotFound extends RuntimeException {
    public static final String ERROR = "CLIENT_NOT_FOUND";
    public ClientNotFound(String message) {
        super(message);
    }
}
