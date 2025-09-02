package br.com.neoapp.api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa uma mensagem de erro específica para um campo de um DTO.
 * <p>
 * Esta classe é utilizada para estruturar os detalhes dos erros de validação
 * em uma resposta de API, indicando qual campo falhou na validação e qual
 * foi a mensagem de erro correspondente.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FieldMessage {
    /**
     * O nome do campo que falhou na validação.
     */
    private String fieldName;
    /**
     * A mensagem de erro descrevendo a falha na validação.
     */
    private String message;
}
