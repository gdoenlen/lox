package com.github.gdoenlen.lox;
sealed interface Statement permits Block, Conditional, Expression, NullStatement, Print, Var, While {
}
