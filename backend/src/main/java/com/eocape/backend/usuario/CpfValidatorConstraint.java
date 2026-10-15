package com.eocape.backend.usuario;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidatorConstraint implements ConstraintValidator<Cpf, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null) {
            return true; // @NotBlank cuida do nulo
        }
        return CpfValidator.isValido(cpf.replaceAll("\\D", ""));
    }
}