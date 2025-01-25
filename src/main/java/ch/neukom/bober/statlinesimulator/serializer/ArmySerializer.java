package ch.neukom.bober.statlinesimulator.serializer;

import ch.neukom.bober.statlinesimulator.data.Army;
import ch.neukom.bober.statlinesimulator.data.Enhancement;
import ch.neukom.bober.statlinesimulator.data.Unit;
import ch.neukom.bober.statlinesimulator.properties.AppProperties;
import com.google.common.collect.Streams;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ArmySerializer {
    private final Consumer<Runnable> withoutSynchronization;

    public ArmySerializer(Consumer<Runnable> withoutSynchronization) {
        this.withoutSynchronization = withoutSynchronization;
    }

    public void write(Iterable<Army> armies) {
        withoutSynchronization.accept(() -> {
            try {
                Path dataPath = Path.of(AppProperties.get().dataPath());
                try (Stream<Path> toDelete = Files.list(dataPath)) {
                    for (Path path : toDelete.toList()) {
                        FileUtils.deleteDirectory(path.toFile());
                    }
                }

                for (Army army : armies) {
                    Path armyPath = dataPath.resolve(army.armyId());
                    FileUtils.deleteDirectory(armyPath.toFile());
                    Files.createDirectory(armyPath);
                    Path armyBoberPath = armyPath.resolve("army.bober");
                    Files.createFile(armyBoberPath);
                    Files.write(armyBoberPath, buildArmyFile(army));
                    for (Unit unit : army.units()) {
                        Path unitBoberPath = armyPath.resolve("%s.bober".formatted(unit.unitId()));
                        Files.write(unitBoberPath, builderUnitFile(unit));
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<String> buildArmyFile(Army army) {
        return Stream.concat(
            Stream.of("Name: %s".formatted(army.getArmyName())),
            army.getArmyData(Army.ArmyData::enhancements)
                .stream()
                .flatMap(Collection::stream)
                .map(Enhancement::getName)
                .map("Enhancement: %s"::formatted)
        ).toList();
    }

    private List<String> builderUnitFile(Unit unit) {
        return Streams.concat(
            Stream.of(
                "Name: %s".formatted(unit.unitName()),
                "Count: %s".formatted(unit.count())
            ),
            unit.enhancements()
                .stream()
                .map(Enhancement::getName)
                .map("Enhancement: %s"::formatted)
        ).toList();
    }
}
