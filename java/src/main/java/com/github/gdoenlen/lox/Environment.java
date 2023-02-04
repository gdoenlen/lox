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

        throw new UndefinedVariableException(token);
    }

    Object get(Token token) {
        if (this.values.containsKey(token.lexeme())) {
            return this.values.get(token.lexeme());
        }

        throw new UndefinedVariableException(token);
    }
}
