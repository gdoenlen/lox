package com.github.gdoenlen.lox;

import java.util.Objects;

class Interpreter {
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
        Object left = this.interpret(b.left());
        Object right = this.interpret(b.right());

        return switch (type) {
            case BANG_EQUAL -> !Objects.equals(left, right);
            case EQUAL_EQUAL -> Objects.equals(left, right);
            case GREATER -> (Double) left > (Double) right;
            case GREATER_EQUAL -> (Double) left >= (Double) right;
            case LESS -> (Double) left < (Double) right;
            case LESS_EQUAL -> (Double) left <= (Double) right;
            case MINUS -> (Double) left - (Double) right;
            case PLUS -> plus(left, right);
            case SLASH -> (Double) left / (Double) right;
            case STAR -> (Double) left * (Double) right;
            default -> throw new IllegalArgumentException(type.toString());
        };
    }

    private static Object plus(Object left, Object right) {
        if (left instanceof String l) {
            return l + right;
        }

        if (right instanceof String r) {
            return r + left;
        }

        if (left instanceof Double l && right instanceof Double r) {
            return l + r;
        }

        throw new IllegalArgumentException(
            String.format(
                "Tried to add or concatenate non-string or non-double classes: %s, %s",
                left.getClass(),
                right.getClass()
            )
        );
    }
}
