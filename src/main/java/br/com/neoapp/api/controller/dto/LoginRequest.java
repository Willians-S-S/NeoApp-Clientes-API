package br.com.neoapp.api.controller.dto;

/**
 * Representa o Data Transfer Object (DTO) para a requisição de login.
 * Este registro encapsula as credenciais (e-mail e senha) enviadas pelo
 * cliente para se autenticar na API.
 *
 * @param email     O e-mail de login do usuário.
 * @param password  A senha de acesso do usuário.
 */
public record LoginRequest(String email, String password) { }
