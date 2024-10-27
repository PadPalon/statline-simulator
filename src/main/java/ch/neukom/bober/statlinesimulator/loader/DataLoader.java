package ch.neukom.bober.statlinesimulator.loader;

import ch.neukom.bober.statlinesimulator.properties.PropertyLoader;
import ch.neukom.bober.statlinesimulator.util.ArrayListMultimapCollector;
import ch.neukom.bober.statlinesimulator.util.MultimapUtil;
import ch.neukom.bober.statlinesimulator.util.StringUtil;
import com.google.common.collect.Multimap;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataLoader {
    private final Map<String, Army> armies = new HashMap<>();

    public void loadOnce() {
        try {
            Path dataPath = Path.of(PropertyLoader.get().dataPath());
            if (!Files.isDirectory(dataPath)) {
                Files.createDirectory(dataPath);
            }

            try (Stream<Path> files = Files.list(dataPath)) {
                files.map(this::handleArmyPath).forEach(army -> armies.put(army.armyId(), army));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Army handleArmyPath(Path armyPath) {
        try {
            ArmyData armyData = loadArmyData(armyPath);
            List<Unit> units = loadUnits(armyPath);
            String armyFileName = armyPath.getFileName().toString();
            return new Army(
                armyFileName,
                armyData == null || armyData.armyName().isEmpty() ? armyFileName : armyData.armyName(),
                units,
                armyData
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ArmyData loadArmyData(Path armyPath) throws IOException {
        try (Stream<Path> files = Files.list(armyPath)) {
            return files
                .filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().equals("army.bober"))
                .findAny()
                .map(this::handleArmyDataPath)
                .orElse(null);
        }
    }

    private ArmyData handleArmyDataPath(Path armyDataPath) {
        try {
            Attributes attributes = loadAttributes(armyDataPath);
            return new ArmyData(
                attributes.get(AttributeLoader.NAME)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Unit> loadUnits(Path armyPath) throws IOException {
        try (Stream<Path> files = Files.list(armyPath)) {
            return files.filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(".bober"))
                .filter(path -> !path.getFileName().toString().startsWith("army."))
                .map(this::handleUnitPath)
                .toList();
        }
    }

    private Unit handleUnitPath(Path unitPath) {
        try {
            Attributes attributes = loadAttributes(unitPath);
            return new Unit(
                attributes.get(AttributeLoader.NAME)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Attributes loadAttributes(Path dataPath) throws IOException {
        Multimap<String, String> data = Files.readAllLines(dataPath)
            .stream()
            .filter(StringUtil::isNotNullOrEmpty)
            .map(this::buildAttribute)
            .collect(ArrayListMultimapCollector.toMultimap(Attribute::key, Attribute::value));
        return new Attributes(data);
    }

    private Attribute buildAttribute(String line) {
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

    public void printData() {
        armies.values().forEach(System.out::println);
    }

    public record Army(String armyId, String armyName, List<Unit> units, ArmyData armyData) {
        @Override
        public String toString() {
            return """
                ---
                Army Name: %s
                Units:
                %s
                ---
                """
                .formatted(
                    armyName(),
                    units().stream().map("- %s"::formatted).collect(Collectors.joining("\n"))
                );
        }
    }

    public record ArmyData(String armyName) {
    }

    public record Unit(String unitName) {
        @Override
        public String toString() {
            return unitName;
        }
    }

    public record Attributes(Multimap<String, String> data) {
        public <R> R get(AttributeLoader<R> loader) {
            return loader.apply(data);
        }
    }

    private record Attribute(String key, String value) {
    }

    public interface AttributeLoader<R> extends Function<Multimap<String, String>, R> {
        AttributeLoader<String> NAME = data -> MultimapUtil.getOnlyValue(data, "NAME").orElse("");
    }
}
