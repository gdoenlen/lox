package com.github.gdoenlen.lox;

import java.util.Collection;

class ClockCallable implements LCallable {
    @Override
    public Object call(Interpreter interpreter, Environment environment, Collection<Object> arguments) {
        return System.currentTimeMillis() / 1000.0;
    }

    @Override
    public int arity() {
        return 0;
    }
}
