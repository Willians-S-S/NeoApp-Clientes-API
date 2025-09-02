package br.com.neoapp.api.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação de validação customizada para verificar se um campo do tipo String
 * representa um número válido (neste contexto, geralmente um número de telefone).
 * <p>
 * A lógica de validação é implementada na classe {@link NumberValidator}, que é
 * vinculada a esta anotação através de {@code @Constraint(validatedBy = ...)}.
 * <p>
 * Exemplo de uso:
 * <pre>
 * {@code
 * @NumberValid
 * private String phone;
 * }
 * </pre>
 */
@Constraint(validatedBy = NumberValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberValid {
    /**
     * @return A mensagem de erro a ser retornada se a validação falhar.
     * O valor padrão é "Número inválido.".
     */
    String message() default "Número inválido.";

    /**
     * @return Os grupos de validação aos quais esta restrição pertence.
     * Permite ativar a validação apenas para certos cenários.
     */
    Class<?>[] groups() default {};

    /**
     * @return A carga útil (payload) que pode ser associada à restrição,
     * frequentemente usada para anexar metadados à validação.
     */
    Class<? extends Payload>[] payload() default {};
}
