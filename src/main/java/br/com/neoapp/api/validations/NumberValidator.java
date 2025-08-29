package br.com.neoapp.api.validations;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NumberValidator implements ConstraintValidator<NumberValid, String> {
    private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

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
