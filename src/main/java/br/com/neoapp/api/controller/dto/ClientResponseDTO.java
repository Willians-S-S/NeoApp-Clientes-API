package br.com.neoapp.api.controller.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record ClientResponseDTO(String id,
                                String name,
                                Integer age,
                                String email,
                                String phone,
                                String cpf,
                                OffsetDateTime creatAt,
                                OffsetDateTime updateAt) { }
