package br.com.neoapp.api.exceptions;

/**
 * Exceção lançada quando uma operação tenta acessar um cliente que não existe
 * no banco de dados.
 * <p>
 * Esta é uma exceção de tempo de execução (unchecked) porque, em um contexto de API REST,
 * um ID de cliente não encontrado geralmente resulta de uma requisição do cliente
 * com um ID inválido, o que deve ser tratado como um erro do lado do cliente (e.g., HTTP 404).
 */
public class ClientNotFound extends RuntimeException {
    public static final String ERROR = "CLIENT_NOT_FOUND";
    public ClientNotFound(String message) {
        super(message);
    }
}
