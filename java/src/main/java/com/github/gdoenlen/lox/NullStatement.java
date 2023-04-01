package com.github.gdoenlen.lox;

final class NullStatement implements Statement {
    private static final NullStatement INSTANCE = new NullStatement();
    private NullStatement() {}
    public static NullStatement instance() {
        return INSTANCE;
    }
}
