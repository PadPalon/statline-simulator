package ch.neukom.bober.statlinesimulator.loader;

import ch.neukom.bober.statlinesimulator.data.Army;
import ch.neukom.bober.statlinesimulator.data.Attributes;
import ch.neukom.bober.statlinesimulator.data.Unit;
import ch.neukom.bober.statlinesimulator.properties.AppProperties;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.nio.file.StandardWatchEventKinds.*;

public class DataLoader {
    public Map<String, Army> loadOnce() {
        try {
            Map<String, Army> armies = new HashMap<>();

            Path dataPath = Path.of(AppProperties.get().dataPath());
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

    public ArmyWatcher watchForChanges() {
        try {
            Path dataPath = Path.of(AppProperties.get().dataPath());
            if (!Files.isDirectory(dataPath)) {
                Files.createDirectory(dataPath);
            }

            WatchService watchService = FileSystems.getDefault().newWatchService();
            WatchKey watchKey = dataPath.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            Files.walkFileTree(dataPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) throws IOException {
                    dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                    return FileVisitResult.CONTINUE;
                }
            });
            return new ArmyWatcher(watchKey, this::loadOnce);
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
                attributes.get(Attributes.AttributeLoader.NAME),
                attributes.get(Attributes.AttributeLoader.ENHANCEMENTS)
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
            String filename = unitPath.getFileName().toString();
            return new Unit(
                filename.substring(0, filename.indexOf(".bober")),
                attributes.get(Attributes.AttributeLoader.NAME),
                attributes.get(Attributes.AttributeLoader.COUNT),
                attributes.get(Attributes.AttributeLoader.ENHANCEMENTS)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
