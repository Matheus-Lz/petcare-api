package com.petcare.petcare_api.infrastructure.utils;

import java.util.regex.Pattern;

public class CpfCnpjUtils {

    private CpfCnpjUtils() {
        throw new IllegalStateException("Classe utilitária não deve ser instanciada.");
    }

    private static final Pattern CPF_PATTERN = Pattern.compile("\\d{11}");
    private static final Pattern CNPJ_PATTERN = Pattern.compile("\\d{14}");

    public static boolean validate(String cpfCnpj) {
        if (cpfCnpj == null) {
            return false;
        }
        if (isCpf(cpfCnpj)) {
            return validateCpf(cpfCnpj);
        } else if (isCnpj(cpfCnpj)) {
            return validateCnpj(cpfCnpj);
        }
        return false;
    }

    private static boolean isCpf(String document) {
        return CPF_PATTERN.matcher(document).matches();
    }

    private static boolean isCnpj(String document) {
        return CNPJ_PATTERN.matcher(document).matches();
    }

    private static boolean validateCpf(String cpf) {
        int sum = 0, remainder;
        for (int i = 0; i < 9; i++) {
            sum += (cpf.charAt(i) - '0') * (10 - i);
        }
        remainder = 11 - (sum % 11);
        if (remainder == 10 || remainder == 11) remainder = 0;
        if (remainder != cpf.charAt(9) - '0') return false;

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (cpf.charAt(i) - '0') * (11 - i);
        }
        remainder = 11 - (sum % 11);
        if (remainder == 10 || remainder == 11) remainder = 0;
        return remainder == cpf.charAt(10) - '0';
    }

    private static boolean validateCnpj(String cnpj) {
        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += (cnpj.charAt(i) - '0') * weights1[i];
        }
        int remainder = sum % 11;
        int digit1 = (remainder < 2) ? 0 : 11 - remainder;
        if (digit1 != cnpj.charAt(12) - '0') return false;

        sum = 0;
        for (int i = 0; i < 13; i++) {
            sum += (cnpj.charAt(i) - '0') * weights2[i];
        }
        remainder = sum % 11;
        int digit2 = (remainder < 2) ? 0 : 11 - remainder;
        return digit2 == cnpj.charAt(13) - '0';
    }
}
