package br.com.neoapp.api.exceptions;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Especialização de {@link StandardError} para lidar com erros de validação de DTOs.
 * <p>
 * Esta classe estende a estrutura de erro padrão para incluir uma lista detalhada
 * de todos os campos que falharam na validação, junto com suas respectivas
 * mensagens de erro. É tipicamente usada para respostas com status HTTP 422 (Unprocessable Entity).
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ValidationError extends StandardError {

    /**
     * Uma lista de objetos {@link FieldMessage}, cada um contendo o nome de um campo
     * e a mensagem de erro de validação associada.
     * <p>
     * A anotação {@code @Builder.Default} garante que a lista seja sempre inicializada.
     */
    @Builder.Default
    private List<FieldMessage> errors = new ArrayList<>();

    /**
     * Adiciona um novo erro de validação à lista de erros.
     *
     * @param fieldName o nome do campo que continha o erro.
     * @param message a mensagem de erro de validação para o campo.
     */
    public void addError(String fieldName, String message) {
        errors.add(new FieldMessage(fieldName, message));
    }
}
