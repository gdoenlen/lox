package com.github.gdoenlen.lox;

class UndefinedVariableException extends RuntimeException {
    private final Token token;

    UndefinedVariableException(Token token, String message) {
        super(message);

        this.token = token;
    }

    Token getToken() {
        return this.token;
    }
}
