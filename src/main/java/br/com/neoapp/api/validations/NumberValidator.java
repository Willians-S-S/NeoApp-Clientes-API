package br.com.neoapp.api.validations;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Implementação do validador para a anotação customizada {@link NumberValid}.
 * <p>
 * Esta classe utiliza a biblioteca {@code libphonenumber} do Google para verificar
 * se uma String representa um número de telefone válido, assumindo a região
 * do Brasil ("BR") como padrão para a análise.
 */
public class NumberValidator implements ConstraintValidator<NumberValid, String> {

    /**
     * Instância singleton da classe utilitária de validação de números de telefone.
     */
    private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    /**
     * Valida se a String fornecida é um número de telefone brasileiro válido.
     * <p>
     * A validação segue as seguintes regras:
     * <ul>
     * <li>Valores nulos ou vazios são considerados válidos. Isso permite que o campo seja opcional.
     * Para tornar o preenchimento obrigatório, a anotação {@code @NotBlank} deve ser usada em conjunto.</li>
     * <li>A String é analisada (parse) e validada de acordo com os padrões de números de telefone do Brasil.</li>
     * </ul>
     *
     * @param value   o valor do campo (o número de telefone em formato String) a ser validado.
     * @param context o contexto no qual a restrição é avaliada (não utilizado nesta implementação).
     * @return {@code true} se o valor for nulo, vazio ou um número de telefone brasileiro válido;
     * {@code false} caso contrário.
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        try {
            Phonenumber.PhoneNumber numeroProto = phoneUtil.parse(value, "BR");
            return phoneUtil.isValidNumber(numeroProto);
        } catch (NumberParseException e) {
            return false;
        }
    }
}
