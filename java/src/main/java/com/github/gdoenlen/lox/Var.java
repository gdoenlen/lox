package com.github.gdoenlen.lox;

record Var(Token token, Expr initializer) implements Statement {
}
