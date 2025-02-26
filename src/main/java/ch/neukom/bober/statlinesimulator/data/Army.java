package ch.neukom.bober.statlinesimulator.data;

import ch.neukom.bober.statlinesimulator.display.Printable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Army(String armyId,
                   List<Unit> units,
                   @Nullable ArmyData armyData) implements Printable {
    @Override
    public String toString() {
        return armyId;
    }

    public String getArmyName() {
        return Optional.ofNullable(armyData).map(ArmyData::armyName).orElse(armyId);
    }

    public <R> Optional<R> getArmyData(Function<ArmyData, R> loader) {
        if (armyData == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(loader.apply(armyData));
    }

    public Army withUnit(Unit unit) {
        List<Unit> newUnits = Stream.concat(
            units.stream(),
            Stream.of(unit)
        ).toList();
        return new Army(armyId, newUnits, armyData);
    }

    public Army replaceUnit(Unit toUpdate, Unit updatedUnit) {
        List<Unit> newUnits = units.stream().map(unit -> {
            if (unit.equals(toUpdate)) {
                return updatedUnit;
            } else {
                return unit;
            }
        }).toList();
        return new Army(armyId, newUnits, armyData);
    }

    public Army withoutUnit(Unit unitToRemove) {
        List<Unit> newUnits = units.stream().filter(unit -> !unit.equals(unitToRemove)).toList();
        return new Army(armyId, newUnits, armyData);
    }

    @Override
    public String print() {
        return """
            Army Name: %s
            Units:
            %s
            """
            .formatted(
                getArmyName(),
                units().stream().map(Unit::print).map("- %s"::formatted).collect(Collectors.joining("\n"))
            );
    }

    public record ArmyData(String armyName, Set<Enhancement> enhancements) {
    }
}
