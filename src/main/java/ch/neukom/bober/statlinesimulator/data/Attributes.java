package ch.neukom.bober.statlinesimulator.data;

import ch.neukom.bober.statlinesimulator.util.ArrayListMultimapCollector;
import ch.neukom.bober.statlinesimulator.util.MultimapUtil;
import ch.neukom.bober.statlinesimulator.util.StringUtil;
import com.google.common.collect.Multimap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

public record Attributes(Multimap<String, String> data) {
    public static Attributes loadAttributes(Path dataPath) throws IOException {
        Multimap<String, String> data = Files.readAllLines(dataPath)
            .stream()
            .filter(StringUtil::isNotNullOrEmpty)
            .map(Attributes::buildAttribute)
            .collect(ArrayListMultimapCollector.toMultimap(Attribute::key, Attribute::value));
        return new Attributes(data);
    }

    private static Attribute buildAttribute(String line) {
        int separatorIndex = line.indexOf(':');
        if (separatorIndex < 0) {
            return new Attribute(
                "UNKNOWN",
                line
            );
        } else {
            return new Attribute(
                line.substring(0, separatorIndex).trim().toUpperCase(),
                line.substring(separatorIndex + 1).trim()
            );
        }
    }

    public <R> R get(AttributeLoader<R> loader) {
        return loader.apply(data);
    }

    public interface AttributeLoader<R> extends Function<Multimap<String, String>, R> {
        AttributeLoader<String> NAME = data -> MultimapUtil.getOnlyValue(data, "NAME").orElse("");
        AttributeLoader<Integer> COUNT = data -> MultimapUtil.getOnlyValue(data, "COUNT").map(Integer::valueOf).orElse(1);
    }

    record Attribute(String key, String value) {
    }
}
