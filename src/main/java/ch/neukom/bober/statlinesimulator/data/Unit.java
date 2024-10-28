package ch.neukom.bober.statlinesimulator.data;

import ch.neukom.bober.statlinesimulator.properties.Properties;
import ch.neukom.bober.statlinesimulator.util.UnsupportedCombiner;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public record Unit(String unitName, int count, Set<Enhancement> enhancements) implements Printable {
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
            Properties.get().baseSkill(),
            enhancement -> enhancement::adjustSkill
        );
    }

    public int getAccuracy(Set<Enhancement> armyEnhancements) {
        return applyEnhancements(
            armyEnhancements,
            Properties.get().baseAccuracy(),
            enhancement -> enhancement::adjustAccuracy
        );
    }

    public int getShots(Set<Enhancement> armyEnhancements) {
        return applyEnhancements(
            armyEnhancements,
            Properties.get().baseShots(),
            enhancement -> enhancement::adjustShots
        );
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
