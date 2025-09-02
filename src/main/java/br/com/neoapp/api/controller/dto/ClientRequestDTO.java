package br.com.neoapp.api.controller.dto;

import br.com.neoapp.api.validations.NumberValid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

/**
 * Representa o Data Transfer Object (DTO) para a criação de um novo cliente.
 * Este registro encapsula todos os dados necessários que um consumidor da API
 * deve fornecer para registrar um cliente, aplicando as devidas validações de entrada.
 *
 * @param name       O nome completo do cliente. Não pode ser vazio e deve ter entre 2 e 100 caracteres.
 * @param birthday   A data de nascimento do cliente. Deve ser uma data no passado.
 * @param email      O endereço de e-mail do cliente. Não pode ser vazio e deve ter um formato de e-mail válido.
 * @param password   A senha de acesso do cliente. Não pode ser vazia e deve ter no mínimo 8 caracteres.
 * @param phone      O número de telefone do cliente. Passa por uma validação customizada (@NumberValid).
 * @param cpf        O Cadastro de Pessoas Físicas (CPF) do cliente. Não pode ser vazio e deve ser um CPF válido.
 */
public record ClientRequestDTO(
                                @NotBlank(message = "O nome não pode ser vazio.")
                                @Size(min = 2, max = 100)
                               String name,
                               @Past(message = "A data de nascimento deve ser no passado.")
                               LocalDate birthday,
                               @NotBlank
                               @Email(message = "Formato de e-mail inválido.")
                               String email,
                               @NotBlank
                               @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
                               String password,
                               @NumberValid
                               String phone,
                                @NotBlank
                                @CPF(message = "CPF inválido.")
                               String cpf) { }
