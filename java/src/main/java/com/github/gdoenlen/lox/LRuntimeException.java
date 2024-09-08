package com.github.gdoenlen.lox;

class LRuntimeException extends RuntimeException {
    protected final transient Token token;

    LRuntimeException(Token token) {
        this.token = token;
    }

    Token token() {
        return this.token;
    }
}
