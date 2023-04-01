package com.github.gdoenlen.lox;

sealed interface Expr permits Assign, Binary, Grouping, Literal, Logical, NullExpr, Unary, Variable {
}
