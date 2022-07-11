package com.github.gdoenlen.lox;

record Unary(Token operator, Expr right) implements Expr {
}
