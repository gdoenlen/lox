package com.github.gdoenlen.lox;


sealed interface Statement permits Block, Conditional, Expression, Func, NullStatement, Print, Var, While {
}
