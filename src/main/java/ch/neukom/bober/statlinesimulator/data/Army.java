package ch.neukom.bober.statlinesimulator.data;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public String print() {
        return """
            ---
            Army Name: %s
            Units:
            %s
            ---
            """
            .formatted(
                getArmyName(),
                units().stream().map(Unit::print).map("- %s"::formatted).collect(Collectors.joining("\n"))
            );
    }

    public record ArmyData(String armyName, Set<Enhancement> enhancements) {
    }
}
