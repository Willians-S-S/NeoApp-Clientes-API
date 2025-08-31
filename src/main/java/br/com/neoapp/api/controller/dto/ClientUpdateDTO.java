package br.com.neoapp.api.controller.dto;

import br.com.neoapp.api.validations.NumberValid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record ClientUpdateDTO(@NotBlank(message = "O nome não pode ser vazio.")
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
                              String cpf) {
}
