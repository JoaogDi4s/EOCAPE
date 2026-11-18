package com.eocape.backend.usuario;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Valida o formato e os dígitos verificadores do CPF.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public class CpfValidatorConstraint implements ConstraintValidator<Cpf, String> {

    /**
     * Executa a validação do CPF para a integração com Jakarta Validation.
     *
     * @param cpf CPF do usuário; a camada de serviço normaliza os dígitos antes da consulta.
     * @param context Contexto do Jakarta Validation usado para informar violações da restrição.
     * @return true se a condição verificada for atendida; false caso contrário.
     */
    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null) {
            return true; // @NotBlank cuida do nulo
        }
        return CpfValidator.isValido(cpf.replaceAll("\\D", ""));
    }
}