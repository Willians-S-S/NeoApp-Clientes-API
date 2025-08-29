package br.com.neoapp.api.exceptions;

public class CpfExistsException extends RuntimeException {
    public static final String ERROR = "CPF_ALREADY_EXISTS";
    public CpfExistsException(String message) {
        super(message);
    }
}
