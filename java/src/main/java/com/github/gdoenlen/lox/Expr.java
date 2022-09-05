package com.github.gdoenlen.lox;

sealed interface Expr permits Assign, Binary, Grouping, Literal, NullExpr, Unary, Variable {
}
