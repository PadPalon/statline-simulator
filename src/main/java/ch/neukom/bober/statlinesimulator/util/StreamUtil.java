package ch.neukom.bober.statlinesimulator.util;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

public class StreamUtil {
    private StreamUtil() {}

    public static <S, M> Function<S, Stream<SourceMapping<S, M>>> mapWithSource(Function<S, Collection<M>> mappingFunction) {
        return source -> mappingFunction.apply(source).stream().map(mapping -> new SourceMapping<>(source, mapping));
    }

    public record SourceMapping<S, M>(S source, M mapping) {}
}
