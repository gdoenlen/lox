package com.github.gdoenlen.lox.util;

import java.util.Iterator;
import java.util.function.BiFunction;

/** Iterator to zip two iterators into one. Assumes they will be the same size. */
public class Ziperator<A, B, T> implements Iterator<T> {
    private final Iterator<A> a;
    private final Iterator<B> b;
    private final BiFunction<A, B, T> mapper;

    public Ziperator(Iterator<A> a, Iterator<B> b, BiFunction<A, B, T> mapper) {
        this.a = a;
        this.b = b;
        this.mapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return this.a.hasNext();
    }

    @Override
    public T next() {
        return this.mapper.apply(this.a.next(), this.b.next());
    }
}
