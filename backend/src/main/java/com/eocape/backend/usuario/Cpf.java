package com.eocape.backend.usuario;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declara a restrição de validação de CPF aplicada aos campos e parâmetros anotados.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Documented
@Constraint(validatedBy = CpfValidatorConstraint.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cpf {

    /**
     * Obtém a mensagem padrão da restrição de validação de CPF.
     *
     * @return mensagem padrão exibida quando a validação do CPF falhar.
     */
    String message() default "CPF inválido";

    /**
     * Obtém os grupos de validação aos quais a restrição pertence.
     *
     * @return grupos de validação aplicáveis à restrição.
     */
    Class<?>[] groups() default {};

    /**
     * Obtém os metadados adicionais associados à restrição de validação.
     *
     * @return classes de metadados adicionais da restrição.
     */
    Class<? extends Payload>[] payload() default {};
}