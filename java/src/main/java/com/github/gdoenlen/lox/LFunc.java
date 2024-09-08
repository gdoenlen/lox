package com.github.gdoenlen.lox;

import java.util.Collection;
import java.util.Map;

import com.github.gdoenlen.lox.util.CollectionUtils;

class LFunc implements LCallable {
    private final Func func;

    LFunc(Func func) {
        this.func = func;
    }

    @Override
    public Object call(Interpreter interpreter, Environment environment, Collection<Object> arguments) {
        CollectionUtils.zip(func.parameters(), arguments, Map::entry)
            .forEach(entry -> environment.define(entry.getKey().lexeme(), entry.getValue()));
        interpreter.interpret(func.body());

        // gross
        return null;
    }

    @Override
    public int arity() {
        return this.func.parameters().size();
    }
}
