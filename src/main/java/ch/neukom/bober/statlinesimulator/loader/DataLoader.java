package ch.neukom.bober.statlinesimulator.loader;

import ch.neukom.bober.statlinesimulator.data.Army;
import ch.neukom.bober.statlinesimulator.data.Attributes;
import ch.neukom.bober.statlinesimulator.data.Unit;
import ch.neukom.bober.statlinesimulator.properties.Properties;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class DataLoader {
    public Map<String, Army> loadOnce() {
        try {
            Map<String, Army> armies = new HashMap<>();

            Path dataPath = Path.of(Properties.get().dataPath());
            if (!Files.isDirectory(dataPath)) {
                Files.createDirectory(dataPath);
            }

            try (Stream<Path> files = Files.list(dataPath)) {
                files.map(this::handleArmyPath).forEach(army -> armies.put(army.armyId(), army));
            }

            return armies;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Army handleArmyPath(Path armyPath) {
        try {
            Army.ArmyData armyData = loadArmyData(armyPath);
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

    @Nullable
    private Army.ArmyData loadArmyData(Path armyPath) throws IOException {
        try (Stream<Path> files = Files.list(armyPath)) {
            return files
                .filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().equals("army.bober"))
                .findAny()
                .map(this::handleArmyDataPath)
                .orElse(null);
        }
    }

    private Army.ArmyData handleArmyDataPath(Path armyDataPath) {
        try {
            Attributes attributes = Attributes.loadAttributes(armyDataPath);
            return new Army.ArmyData(
                attributes.get(Attributes.AttributeLoader.NAME)
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
            Attributes attributes = Attributes.loadAttributes(unitPath);
            return new Unit(
                attributes.get(Attributes.AttributeLoader.NAME),
                attributes.get(Attributes.AttributeLoader.COUNT)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
