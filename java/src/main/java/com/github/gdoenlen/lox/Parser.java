package com.github.gdoenlen.lox;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

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

        return expr;
    }

    private Expr comparison() {
        Expr expr = this.term();

        while (this.match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            expr = new Binary(expr, this.previous(), this.term());
        }

        return expr;
    }

    private Expr term() {
        Expr expr = this.factor();

        while(this.match(MINUS, PLUS)) {
            expr = new Binary(expr, this.previous(), this.factor());
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = this.unary();

        while (this.match(SLASH, STAR)) {
            expr = new Binary(expr, this.previous(), this.unary());
        }

        return expr;
    }

    private Expr unary() {
        if (this.match(BANG, MINUS)) {
            return new Unary(this.previous(), this.unary());
        }

        return this.primary();
    }

    private Expr primary() {
        if (this.match(FALSE)) {
            return new Literal(Boolean.FALSE);
        }

        if (this.match(TRUE)) {
            return new Literal(Boolean.TRUE);
        }

        if (this.match(NUMBER, STRING)) {
            return new Literal(this.previous().literal());
        }

        if (this.match(LEFT_PAREN)) {
            Expr expr = this.expression();
            this.consume(RIGHT_PAREN, "Expect right parens");

            return new Grouping(expr);
        }

        throw new RuntimeException("todo");
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return this.advance();
        }

        throw this.error(this.peek(), message);
    }

    private ParseException error(Token token, String message) {
        Main.error(-1, message);

        return new ParseException();
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (this.check(type)) {
                // todo can we get out of this side effect here?
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
        return this.peek().tokenType() != EOF;
    }

    private Token peek() {
        return this.tokens.get(this.current);
    }

    private Token advance() {
        if (this.hasNext()) {
            this.current++;
        }

        return this.previous();
    }

    private Token previous() {
        return this.tokens.get(current - 1);
    }

    private void synchronize() {
        this.advance();

        while (this.hasNext()) {
            if (this.previous().tokenType() == SEMI_COLON) {
                return;
            }

            if (KEYWORD_SYNCRHONIZERS.contains(this.peek().tokenType())) {
                return;
            }

            this.advance();
        }
    }

    private static final Set<TokenType> KEYWORD_SYNCRHONIZERS
        = EnumSet.of(CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN);

    Expr parse() {
        try {
            return this.expression();
        } catch (ParseException ex) {
            return null;
        }
    }
}
