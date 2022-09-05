package com.github.gdoenlen.lox;

final class NullExpr implements Expr {
    private static final NullExpr INSTANCE = new NullExpr();

    private NullExpr() {

    }

    public static NullExpr instance() {
        return INSTANCE;
    }
}
