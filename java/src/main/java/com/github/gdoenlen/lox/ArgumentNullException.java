package com.github.gdoenlen.lox;

class ArgumentNullException extends LRuntimeException {
    ArgumentNullException(Token token) {
        super(token);
    }
}
