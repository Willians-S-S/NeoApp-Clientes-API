package br.com.neoapp.api.controller.dto;

import java.time.LocalDate;

public record ClientRequestDTO(String name,
                               LocalDate birthday,
                               String email,
                               String password,
                               String phone,
                               String cpf) { }
