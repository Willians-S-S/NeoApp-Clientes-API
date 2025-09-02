package br.com.neoapp.api.exceptions;

/**
 * Exceção lançada durante o processo de autenticação quando as credenciais fornecidas
 * (e-mail ou senha) são inválidas ou não correspondem a um usuário cadastrado.
 * <p>
 * Por razões de segurança, a mensagem de erro geralmente é genérica para não informar
 * a um potencial invasor se o e-mail existe ou se apenas a senha está incorreta.
 */
public class EmailOrPassworInvalid extends RuntimeException {
    public static final String ERROR = "INVALID_EMAIL_OR_PASSWORD";

    public EmailOrPassworInvalid(String message) {
        super(message);
    }
}
