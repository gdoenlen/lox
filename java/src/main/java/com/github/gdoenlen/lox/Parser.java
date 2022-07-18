package com.github.gdoenlen.lox;

import java.util.List;

import static com.github.gdoenlen.lox.TokenType.*;

class Parser {
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Expr expression() {
        return this.equality();
    }

    private Expr equality() {
        Expr expr = this.comparison();

        while (this.match(BANG_EQUAL, EQUAL_EQUAL)) {
            expr = new Binary(expr, this.previous(), this.comparison());
        }
    }

    private Expr comparison() {
        // todo
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (this.check(type)) {
                this.advance();

                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (!this.hasNext()) {
            return false;
        }

        return this.peek().tokenType() == type;
    }

    private boolean hasNext() {

    }

    private Token advance() {
        if (!this.hasNext()) {
            this.current++;
        }

        return this.previous();
    }

    private Token previous() {
        return this.tokens.get(current - 1);
    }
}
