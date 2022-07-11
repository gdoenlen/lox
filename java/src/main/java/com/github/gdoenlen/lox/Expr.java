package com.github.gdoenlen.lox;

sealed interface Expr permits Binary, Grouping, Literal, Unary {
}
