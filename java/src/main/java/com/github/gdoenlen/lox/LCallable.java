package com.github.gdoenlen.lox;

import java.util.Collection;

interface LCallable {
    Object call(Interpreter interpreter, Environment environment, Collection<Object> arguments);
    int arity();
}
