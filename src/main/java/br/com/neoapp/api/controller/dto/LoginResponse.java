package br.com.neoapp.api.controller.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
}
