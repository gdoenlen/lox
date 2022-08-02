package com.github.gdoenlen.lox;

import static com.github.gdoenlen.lox.TokenType.MINUS;

public class Interpreter {
    Object interpret(Expr expr) {
        return switch (expr) {
            case Literal l -> l.value();
            case Grouping g -> this.interpret(g.expr());
            case Unary u -> this.unary(u);
            case Binary b -> this.binary(b);
        };
    }

    // TODO can we find a way to exhaust the switches on type?
    private Object unary(Unary u) {
        TokenType type = u.operator().tokenType();
        Object right = this.interpret(u.right());

        return switch (type) {
            case BANG -> !isTruthy(right);
            case MINUS -> -(double) right;
            default -> throw new IllegalArgumentException(type.toString());
        };
    }

    private static Boolean isTruthy(Object o) {
        if (o instanceof Boolean b) {
            return b;
        }

        return o != null;
    }

    private Object binary(Binary b) {
        TokenType type = b.operator().tokenType();
        var left = (double) this.interpret(b.left());
        var right = (double) this.interpret(b.right());

        return switch (type) {
            case MINUS -> left - right;
            case SLASH -> left / right;
            case STAR -> left * right;
            default -> throw new IllegalArgumentException(type.toString());
        };
    }
}
