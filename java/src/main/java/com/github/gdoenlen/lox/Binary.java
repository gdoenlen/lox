package com.github.gdoenlen.lox;

record Binary(Expr left, Token operator, Expr right) implements Expr {
}
