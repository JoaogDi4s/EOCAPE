package com.eocape.backend.usuario;

/**
 * Valida o formato e os dígitos verificadores do CPF.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public final class CpfValidator {

    /**
     * Inicializa CpfValidator com os dados e as dependências necessários à operação.
     */
    private CpfValidator() {}

    /**
     * Verifica o formato e os dígitos verificadores do CPF informado.
     *
     * @param cpf CPF do usuário; a camada de serviço normaliza os dígitos antes da consulta.
     * @return true se a condição verificada for atendida; false caso contrário.
     */
    public static boolean isValido(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            return false;
        }

        // Rejeita sequências repetidas: 000.000.000-00, 111.111.111-11, etc.
        if (cpf.chars().distinct().count() == 1) {
            return false;
        }

        for (int t = 9; t < 11; t++) {
            int soma = 0;
            for (int i = 0; i < t; i++) {
                soma += (cpf.charAt(i) - '0') * (t + 1 - i);
            }
            int digito = (soma * 10) % 11 % 10;
            if (digito != cpf.charAt(t) - '0') {
                return false;
            }
        }
        return true;
    }
}