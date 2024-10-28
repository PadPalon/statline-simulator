package ch.neukom.bober.statlinesimulator.data;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public record Army(String armyId,
                   String armyName,
                   List<Unit> units,
                   @Nullable ArmyData armyData) implements Printable {
    @Override
    public String toString() {
        return armyId;
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
                armyName(),
                units().stream().map(Unit::print).map("- %s"::formatted).collect(Collectors.joining("\n"))
            );
    }

    public record ArmyData(String armyName) {
    }
}
