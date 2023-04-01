package com.github.gdoenlen.lox;

import static com.github.gdoenlen.lox.TokenType.AND;
import static com.github.gdoenlen.lox.TokenType.OR;

record Logical(Expr left, Token operator, Expr right) implements Expr {
    boolean isOr() {
        return operator.tokenType() == OR;
    }

    boolean isAnd() {
        return operator.tokenType() == AND;
    }
}
