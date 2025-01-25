package ch.neukom.bober.statlinesimulator.data;

import ch.neukom.bober.statlinesimulator.display.Printable;
import ch.neukom.bober.statlinesimulator.properties.AppProperties;
import ch.neukom.bober.statlinesimulator.util.UnsupportedCombiner;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public record Unit(String unitId, String unitName, int count, Set<Enhancement> enhancements) implements Printable {
    @Override
    public String toString() {
        return unitName;
    }

    @Override
    public String print() {
        return unitName;
    }

    public int getSkill(Set<Enhancement> armyEnhancements) {
        return applyEnhancements(
            armyEnhancements,
            AppProperties.get().baseSkill(),
            enhancement -> enhancement::adjustSkill
        );
    }

    public int getAccuracy(Set<Enhancement> armyEnhancements) {
        return applyEnhancements(
            armyEnhancements,
            AppProperties.get().baseAccuracy(),
            enhancement -> enhancement::adjustAccuracy
        );
    }

    public int getShots(Set<Enhancement> armyEnhancements) {
        return applyEnhancements(
            armyEnhancements,
            AppProperties.get().baseShots(),
            enhancement -> enhancement::adjustShots
        );
    }

    public Unit withEnhancement(Enhancement enhancement) {
        Set<Enhancement> newEnhancements = Stream.concat(
            enhancements().stream(),
            Stream.of(enhancement)
        ).collect(toSet());
        return new Unit(unitId, unitName, count, newEnhancements);
    }

    public Unit withoutEnhancement(Enhancement enhancementToRemove) {
        Set<Enhancement> newEnhancements = enhancements().stream()
            .filter(enhancement -> !enhancement.equals(enhancementToRemove))
            .collect(toSet());
        return new Unit(unitId, unitName, count, newEnhancements);
    }

    private <R> R applyEnhancements(
        Set<Enhancement> armyEnhancements,
        R baseCharacteristic,
        Function<Enhancement, Function<R, R>> enhancementFunction
    ) {
        return Stream.concat(
                armyEnhancements.stream(),
                enhancements().stream()
            )
            .distinct()
            .reduce(
                baseCharacteristic,
                (characteristic, enhancement) -> enhancementFunction.apply(enhancement).apply(characteristic),
                new UnsupportedCombiner<>()
            );
    }
}
