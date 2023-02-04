package com.github.gdoenlen.lox;

import java.util.HashMap;
import java.util.Map;

// TODO: this should probably be an interface
// so we don't have to pay the cost of the conditional
// on every action. most environments are not going to
// have an enclosing scope.
class Environment {
    private final Map<String, Object> values = new HashMap<>();
    private final Environment enclosing;

    Environment() {
        this.enclosing = null;
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    void define(String name, Object value) {
        this.values.put(name, value);
    }

    void assign(Token token, Object value) {
        if (this.values.containsKey(token.lexeme())) {
            this.values.put(token.lexeme(), value);
        }

        if (this.enclosing != null) {
            this.enclosing.assign(token, value);

            return;
        }

        throw new UndefinedVariableException(token);
    }

    Object get(Token token) {
        if (this.values.containsKey(token.lexeme())) {
            return this.values.get(token.lexeme());
        }

        if (this.enclosing != null) {
            return this.enclosing.get(token);
        }

        throw new UndefinedVariableException(token);
    }
}
