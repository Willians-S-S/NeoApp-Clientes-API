package br.com.neoapp.api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Classe base para a padronização de respostas de erro na API.
 * <p>
 * Fornece uma estrutura consistente para todos os erros retornados aos clientes,
 * contendo informações essenciais para a depuração e tratamento do erro.
 * A anotação {@code @SuperBuilder} permite a criação de instâncias de forma fluente.
 */
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class StandardError {

    /**
     * O instante em que o erro ocorreu.
     */
    private Instant timestamp;
    /**
     * O código de status HTTP (e.g., 404, 500).
     */
    private Integer status;
    /**
     * Uma breve descrição do tipo de erro (e.g., "Not Found", "Validation Error").
     */
    private String error;
    /**
     * A mensagem detalhada e legível descrevendo o erro.
     */
    private String message;
    /**
     * O caminho (URI) da requisição que resultou no erro.
     */
    private String path;
}
