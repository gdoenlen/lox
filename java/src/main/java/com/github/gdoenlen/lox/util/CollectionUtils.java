package com.github.gdoenlen.lox.util;

import java.util.Collection;
import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliterator;

public class CollectionUtils {
    private CollectionUtils() {}

    /** Zip two collections into one. Assumes they are the same size */
    public static <A, B, R> Stream<R> zip(Collection<A> a, Collection<B> b, BiFunction<A, B, R> mapper) {
        var ziperator = new Ziperator<>(a.iterator(), b.iterator(), mapper);
        Spliterator<R> spliterator = spliterator(ziperator, a.size(), ORDERED);

        return StreamSupport.stream(spliterator, false);
    }
}
