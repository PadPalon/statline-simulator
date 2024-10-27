package ch.neukom.bober.statlinesimulator.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ArrayListMultimapCollector<T, K, V> implements Collector<T, Multimap<K, V>, Multimap<K, V>> {
    private final Function<T, K> keyFunction;
    private final Function<T, V> valueFunction;

    private ArrayListMultimapCollector(Function<T, K> keyFunction, Function<T, V> valueGetter) {
        this.keyFunction = keyFunction;
        this.valueFunction = valueGetter;
    }

    public static <T, K, V> ArrayListMultimapCollector<T, K, V> toMultimap(Function<T, K> keyGetter, Function<T, V> valueGetter) {
        return new ArrayListMultimapCollector<>(keyGetter, valueGetter);
    }

    public static <T, K> ArrayListMultimapCollector<T, K, T> toMultimap(Function<T, K> keyGetter) {
        return toMultimap(keyGetter, Function.identity());
    }

    @Override
    public Supplier<Multimap<K, V>> supplier() {
        return ArrayListMultimap::create;
    }

    @Override
    public BiConsumer<Multimap<K, V>, T> accumulator() {
        return (map, element) -> map.put(keyFunction.apply(element), valueFunction.apply(element));
    }

    @Override
    public BinaryOperator<Multimap<K, V>> combiner() {
        return (left, right) -> {
            left.putAll(right);
            return left;
        };
    }

    @Override
    public Function<Multimap<K, V>, Multimap<K, V>> finisher() {
        return map -> map;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return ImmutableSet.of(Characteristics.IDENTITY_FINISH);
    }
}
