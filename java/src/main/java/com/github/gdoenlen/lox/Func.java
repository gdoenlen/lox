package com.github.gdoenlen.lox;

import java.util.Collection;

sealed interface Func extends Statement permits StaticFunc {
    Collection<Token> parameters();
    Block body();
}
