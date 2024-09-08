package com.github.gdoenlen.lox;

class ArgumentArityException extends RuntimeException {
    @SuppressWarnings("java:S1948")
    private final Token token;

    ArgumentArityException(Token token, int arity, int numberOfArgumentsReceived) {
        super("Expected " + arity + " arguments, but received " + numberOfArgumentsReceived + " arguments.");

        this.token = token;
    }

    Token token() {
        return this.token;
    }
}
