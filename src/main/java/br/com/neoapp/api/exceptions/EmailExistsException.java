package br.com.neoapp.api.exceptions;

/**
 * Exceção lançada ao tentar cadastrar um cliente com um e-mail que já existe na base de dados.
 * <p>
 * Esta é uma exceção de tempo de execução (unchecked) pois representa uma falha de validação
 * de negócio, geralmente resultando em uma resposta de erro para o cliente (e.g., HTTP 409 Conflict).
 */
public class EmailExistsException extends RuntimeException {
    public static final String ERROR = "EMAIL_ALREADY_EXISTS";
    public EmailExistsException(String message) {
        super(message);
    }
}
