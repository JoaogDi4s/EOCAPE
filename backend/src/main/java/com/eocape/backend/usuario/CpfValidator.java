package com.eocape.backend.usuario;

public final class CpfValidator {

    private CpfValidator() {}

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