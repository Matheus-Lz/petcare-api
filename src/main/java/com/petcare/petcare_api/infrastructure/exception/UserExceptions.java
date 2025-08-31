package com.petcare.petcare_api.infrastructure.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserExceptions {

    public static class CurrentPasswordRequiredException extends RuntimeException {
        public CurrentPasswordRequiredException() {
            super("Senha atual é obrigatória para alterar a senha");
        }
    }

    public static class InvalidCurrentPasswordException extends RuntimeException {
        public InvalidCurrentPasswordException() {
            super("Senha atual incorreta");
        }
    }

    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException() {
            super("Token inválido ou expirado");
        }
    }
}
