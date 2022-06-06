package com.github.gdoenlen.lox;

record Token(TokenType tokenType, String lexeme, Object literal, int line) {

}
