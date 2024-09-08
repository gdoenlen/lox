package com.github.gdoenlen.lox;

class IllegalCallException extends LRuntimeException {
    IllegalCallException(Token token) {
        super(token);
    }
}
