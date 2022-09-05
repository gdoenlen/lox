package com.github.gdoenlen.lox;

import java.util.HashMap;
import java.util.Map;

class Environment {
    private final Map<String, Object> values = new HashMap<>();

    void define(String name, Object value) {
        this.values.put(name, value);
    }

    void assign(Token token, Object value) {
        if (this.values.containsKey(token.lexeme())) {
            this.values.put(token.lexeme(), value);
        }

        throw newUndefinedVariableException(token);
    }

    Object get(Token token) {
        Object value = this.values.get(token.lexeme());
        if (value != null) {
            return value;
        }

        throw newUndefinedVariableException(token);
    }

    private static UndefinedVariableException newUndefinedVariableException(Token token) {
       return new UndefinedVariableException(token, "Undefined variable '" + token.lexeme() + "'.");
    }
}
