package com.github.gdoenlen.lox;

import java.util.ArrayList;
import java.util.Collection;

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
        var tokens = new ArrayList<Token>();
        while (this.hasNext()) {
            this.start = current;
            this.scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, this.line));
        return tokens;
    }

    private boolean hasNext() {
        return this.current < source.length();
    }
}
