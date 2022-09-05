package com.github.gdoenlen.lox;

sealed interface Statement permits Expression, Print, Var {
}
