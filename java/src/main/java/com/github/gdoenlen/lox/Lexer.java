package com.github.gdoenlen.lox;

import java.util.ArrayList;
import java.util.Collection;

import static com.github.gdoenlen.lox.TokenType.*;

class Lexer {
    private final String source;
    private final Collection<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    Lexer(String source) {
        this.source = source;
    }

    Collection<Token> scan() {
        while (this.hasNext()) {
            this.start = current;
            this.scanToken();
        }

        this.tokens.add(new Token(EOF, "", null, this.line));

        return tokens;
    }

    private boolean hasNext() {
        return this.current < source.length();
    }

    private void scanToken() {
        TokenType type;
        char c = this.advance();
        try {
            type = this.typeOf(c);
        } catch (IllegalArgumentException ex) {
            Main.error(this.line, "Unexpected character: " + c);

            return;
        }

        if (type == COMMENT || type == WHITE_SPACE) {
            return;
        }

        if (type == EOL) {
            this.line++;
        }

        Object value = null;
        if (type == STRING) {
            value = this.string();
        }

        this.addToken(type, value);
    }

    private char advance() {
        return source.charAt(this.current++);
    }

    private void addToken(TokenType type) {
        this.addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = this.source.substring(this.start, this.current);
        this.tokens.add(new Token(type, text, literal, this.line));
    }

    private TokenType typeOf(char c) {
        return switch (c) {
            case '(' -> LEFT_PAREN;
            case ')' -> RIGHT_PAREN;
            case '{' -> LEFT_BRACE;
            case '}' -> RIGHT_BRACE;
            case ',' -> COMMA;
            case '.' -> DOT;
            case '-' -> MINUS;
            case '+' -> PLUS;
            case ';' -> SEMI_COLON;
            case '*' -> STAR;
            case '!' -> this.match('=') ? BANG_EQUAL : BANG;
            case '=' -> this.match('=') ? EQUAL_EQUAL : EQUAL;
            case '<' -> this.match('=') ? LESS_EQUAL : LESS;
            case '>' -> this.match('=') ? GREATER_EQUAL : GREATER;
            case '/' -> {
                // todo move the advancement logic to the scan function
                if (this.match('/')) {
                    while (this.peek() != '\n' && !this.hasNext()) {
                        this.advance();
                    }

                    yield COMMENT;
                } else {
                    yield SLASH;
                }
            };
            case ' ', '\r', '\t' -> WHITE_SPACE;
            case '\n' -> EOL;
            case '"' -> STRING;
            default -> throw new IllegalArgumentException("TODO");
        };
    }

    private boolean match(char c) {
        if (this.peek() != c) {
            return false;
        }

        this.current++;

        return true;
    }

    private char peek() {
        if (!this.hasNext()) {
            return '\0';
        }

        return this.source.charAt(this.current);
    }

    private String string() {
        while (this.peek() != '"' && this.hasNext()) {
            if (this.peek() == '\n') {
                this.line++;
            }
            this.advance();
        }

        if (!this.hasNext()) {
            Main.error(this.line, "Unterminated string.");
        }

        // The close ".
        this.advance();

        return this.source.substring(start + 1, current - 1);
    }
}
