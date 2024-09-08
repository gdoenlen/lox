package com.github.gdoenlen.lox;

sealed interface Expr permits Assign, Binary, Call, Grouping, Literal, Logical, NullExpr, Unary, Variable {
}
