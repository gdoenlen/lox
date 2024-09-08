package com.github.gdoenlen.lox;

import java.util.Collection;

record Call(Expr callee, Collection<Expr> arguments, Token callSite) implements Expr {
}
