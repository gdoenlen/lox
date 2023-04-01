package com.github.gdoenlen.lox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static com.github.gdoenlen.lox.TokenType.*;

/**
 * Lox's grammar is defined as:
 *
 * program -> declaration* EOF ;
 * declaration -> varDecl | statement;
 * varDecl -> "var" IDENTIFIER ( "=" expression )? ";" ;
 * statement -> exprStmt | ifStmt | printStmt | block ;
 * exprStmt -> expression ";" ;              todo: go back and define expression
 * ifStmt -> "if" "(" expression ")" statement ( "else" statement )? ;
 * printStmt -> "print" expression ";" ;
 * block -> "{" declaration* "}" ;
 * expression -> assignment ;
 * assignment -> IDENTIFIER "=" assignment | logical_or ;
 * logic_or -> logic_And ( "or" logic_and )* ;
 * logic_and -> equality ( "and" equality )* ;
 */
class Parser {
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Expr expression() {
        return this.assignment();
    }

    private Expr assignment() {
        Expr expr = this.or();

        if (this.match(EQUAL)) {
            Token equals = this.previous();
            Expr value = this.assignment();

            if (expr instanceof Variable v) {
                Token t = v.token();

                return new Assign(t, value);
            }

            throw error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Expr or() {
        Expr expr = this.and();
        while(this.match(OR)) {
            Token operator = this.previous();
            Expr right = this.and();
            expr = new Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr and() {
        Expr expr = this.equality();
        while (this.match(AND)) {
            Token operator = this.previous();
            Expr right = this.equality();
            expr = new Logical(expr, operator, right);
        }

        return expr;
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

        if (this.match(NIL)) {
            return NullExpr.instance();
        }

        if (this.match(NUMBER, STRING)) {
            return new Literal(this.previous().literal());
        }

        if (this.match(LEFT_PAREN)) {
            Expr expr = this.expression();
            this.consume(RIGHT_PAREN, "Expect right parens");

            return new Grouping(expr);
        }

        if (this.match(IDENTIFIER)) {
            return new Variable(this.previous());
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

    Collection<Statement> parse() {
        var statements = new ArrayList<Statement>();
        while (this.hasNext()) {
            try {
                statements.add(this.declaration());
            } catch (ParseException ex) {
                this.synchronize();
            }
        }

        return statements;
    }

    private Statement declaration() {
        if (this.match(VAR)) {
            return this.variableDeclaration();
        }

        return this.statement();
    }

    private Statement variableDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name.");

        Expr initializer = NullExpr.instance();
        if (this.match(EQUAL)) {
            initializer = this.expression();
        }

        consume(SEMI_COLON, "Expect ';' after variable declaration.");

        return new Var(name, initializer);
    }

    private Statement statement() {
        if (this.match(IF)) {
            return this.conditional();
        }
        if (this.match(PRINT)) {
            return this.printStatement();
        }

        if (this.match(LEFT_BRACE)) {
            return this.block();
        }

        return this.expressionStatement();
    }

    private Statement conditional() {
        this.consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr expr = this.expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");

        Statement thenBranch = this.statement();
        Statement elseBranch = NullStatement.instance();
        if (this.match(ELSE)) {
            elseBranch = this.statement();
        }

        return new Conditional(expr, thenBranch, elseBranch);
    }

    private Statement printStatement() {
        Expr value = this.expression();
        this.consume(SEMI_COLON, "Expect ';' after value.");

        return new Print(value);
    }

    private Statement block() {
        var statements = new ArrayList<Statement>();

        while (!this.check(RIGHT_BRACE) && this.hasNext()) {
            statements.add(this.declaration());
        }

        this.consume(RIGHT_BRACE, "Expect '}' after block.");

        return new Block(statements);
    }

    private Statement expressionStatement() {
        Expr expr = this.expression();
        consume(SEMI_COLON, "Expect ';' after expression.");

        return new Expression(expr);
    }
}
