package com.github.gdoenlen.lox;

record Conditional(Expr condition, Statement thenBranch, Statement elseBranch) implements Statement {
}
