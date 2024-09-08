package com.github.gdoenlen.lox;

import java.util.Collection;

record StaticFunc(Token name, Collection<Token> parameters, Block body) implements Func {
    int arity() {
        return this.parameters.size();
    }
}
