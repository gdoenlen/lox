package com.github.gdoenlen.lox;

import java.util.Map;
import java.util.Objects;

import com.github.gdoenlen.lox.util.CollectionUtils;

class Interpreter {
    private static final Environment GLOBALS = new Environment();
    static {
        // todo how do we do native funcs without classes?
        GLOBALS.define("clock", new ClockCallable());
    }
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
            case Call c -> {
                Object callee = this.interpret(c.callee());
                if (!(callee instanceof StaticFunc fn)) {
                    throw new IllegalCallException(c.callSite());
                }

                if (fn.arity() != c.arguments().size()) {
                    throw new ArgumentArityException(c.callSite(), fn.arity(), c.arguments().size());
                }

                // todo this should probably be an autocloseable of some sort
                var previousEnv = this.environment;
                this.environment = new Environment(environment);
                try {
                    var arguments = c.arguments()
                        .stream()
                        .map(this::interpret)
                        .toList();
                    CollectionUtils.zip(fn.parameters(), arguments, Map::entry)
                        .forEach(entry -> environment.define(entry.getKey().lexeme(), entry.getValue()));

                    this.interpret(fn.body());

                    // todo we need return values
                    yield null;
                } finally {
                    this.environment = previousEnv;
                }
            }
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

        // todo this is a bug, objects should always be added l + r, not r + l
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
                // todo make an autocloseable for environ
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
            case StaticFunc sf -> this.environment.define(sf.name().lexeme(), sf);
            case While(var condition, var body) -> {
                while (isTruthy(this.interpret(condition))) {
                    this.interpret(body);
                }
            }
            case NullStatement ns -> {}
        }
    }
}
