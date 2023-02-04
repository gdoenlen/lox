package com.github.gdoenlen.lox;

import java.util.Collection;

record Block(Collection<Statement> statements) implements Statement {
}
