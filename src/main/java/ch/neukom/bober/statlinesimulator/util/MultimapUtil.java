package ch.neukom.bober.statlinesimulator.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Optional;

public class MultimapUtil {
    private MultimapUtil() {}

    public static <K, R> Optional<R> getOnlyValue(Multimap<K, R> multimap, K key) {
        Collection<R> values = multimap.get(key);
        if (values.size() == 1) {
            return Optional.of(Iterables.getOnlyElement(values));
        } else {
            return Optional.empty();
        }
    }
}
