package com.github.gdoenlen.lox;

import java.util.Objects;

class Interpreter {
    private Environment environment = new Environment();

    @SuppressWarnings("unused")
    private Object interpret(Expr expr) {
        return switch (expr) {
            case Assign assign -> {
                Object value = this.interpret(assign.value());
                this.environment.assign(assign.token(), value);

                yield value;
            }
            case Binary b -> this.binary(b);
            case Grouping g -> this.interpret(g.expr());
            case Literal l -> l.value();
            case Logical l -> {
                Object left = this.interpret(l.left());
                if (l.isOr() && isTruthy(left)) {
                    yield left;
                }

                if (l.isAnd() && !isTruthy(left)) {
                    yield left;
                }

                yield this.interpret(l.right());
            }
            case NullExpr nullExpr -> null;
            case Unary u -> this.unary(u);
            case Variable variable -> this.environment.get(variable.token());
        };
    }

    // TODO can we find a way to exhaust the switches on type?
    private Object unary(Unary u) {
        TokenType type = u.tokenType();
        Object right = this.interpret(u.right());

        return switch (type) {
            case BANG -> !isTruthy(right);
            case MINUS -> -(double) right;
            default -> throw new IllegalArgumentException(type.toString());
        };
    }

    private static boolean isTruthy(Object o) {
        if (o instanceof Boolean b) {
            return b;
        }

        return o != null;
    }

    private Object binary(Binary b) {
        TokenType type = b.tokenType();
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

    @SuppressWarnings({
        // s.out
        "java:S106",
        // switch default, false positive
        "java:S131",
        // empty case block
        "java:S108",
        "unused"
    })
    void interpret(Statement statement) {
        switch (statement) {
            case Expression e -> this.interpret(e.expr());
            case Print p -> System.out.println(Objects.toString(this.interpret(p.value()), "nil"));
            case Var v -> this.environment.define(v.token().lexeme(), this.interpret(v.initializer()));
            case Block b -> {
                var previousEnv = this.environment;
                try {
                    this.environment = new Environment(previousEnv);
                    b.statements().forEach(this::interpret);
                } finally {
                    this.environment = previousEnv;
                }
            }
            case Conditional c -> {
                if (isTruthy(this.interpret(c.condition()))) {
                    this.interpret(c.thenBranch());
                } else {
                    this.interpret(c.elseBranch());
                }
            }
            case While w -> {
                while (isTruthy(this.interpret(w.condition()))) {
                    this.interpret(w.body());
                }
            }
            case NullStatement ns -> {}
        }
    }
}
