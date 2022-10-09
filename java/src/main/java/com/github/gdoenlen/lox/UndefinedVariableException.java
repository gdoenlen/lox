package com.github.gdoenlen.lox;

class UndefinedVariableException extends RuntimeException {
    private final Token token;

    UndefinedVariableException(Token token) {
        super("Undefined variable: " + token.lexeme());

        this.token = token;
    }

    Token getToken() {
        return this.token;
    }
}
