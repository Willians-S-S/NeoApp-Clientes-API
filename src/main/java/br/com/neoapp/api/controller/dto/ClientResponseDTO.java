package br.com.neoapp.api.controller.dto;

import java.time.OffsetDateTime;

/**
 * Representa o Data Transfer Object (DTO) para a resposta de dados de um cliente.
 * Este registro é usado para expor informações seguras e formatadas de um cliente
 * para os consumidores da API, omitindo dados sensíveis como senhas.
 *
 * @param id        O identificador único (UUID) do cliente.
 * @param name      O nome completo do cliente.
 * @param age       A idade do cliente, calculada a partir da data de nascimento.
 * @param email     O endereço de e-mail do cliente.
 * @param phone     O número de telefone do cliente.
 * @param cpf       O Cadastro de Pessoas Físicas (CPF) do cliente.
 * @param creatAt   A data e hora em que o registro do cliente foi criado.
 * @param updateAt  A data e hora da última atualização no registro do cliente.
 */
public record ClientResponseDTO(String id,
                                String name,
                                Integer age,
                                String email,
                                String phone,
                                String cpf,
                                OffsetDateTime creatAt,
                                OffsetDateTime updateAt) { }
