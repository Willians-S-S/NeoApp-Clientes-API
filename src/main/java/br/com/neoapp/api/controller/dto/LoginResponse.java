package br.com.neoapp.api.controller.dto;

/**
 * Representa o Data Transfer Object (DTO) para a resposta de um login bem-sucedido.
 * Este registro encapsula o token de acesso gerado e seu tempo de expiração,
 * que são retornados ao cliente após a autenticação.
 *
 * @param accessToken O token de acesso (JWT) gerado para o usuário autenticado.
 * @param expiresIn   O tempo de validade do token em segundos.
 */
public record LoginResponse(String accessToken, Long expiresIn) {
}
