package com.petcare.petcare_api.infrastructure.exception;


public class UserExceptions {

    private UserExceptions() {
        throw new UnsupportedOperationException("Utility class");
    }

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

    public static class TokenGenerationException extends RuntimeException {
        public TokenGenerationException() {
            super("Erro ao gerar token");
        }
    }

    public static class UserAuthoritiesFetchException extends RuntimeException {
        public UserAuthoritiesFetchException(String userId) {
            super("Erro ao consultar permissões do usuário " + userId);
        }
    }
}
