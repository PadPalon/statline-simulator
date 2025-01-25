package ch.neukom.bober.statlinesimulator.statistics;

import ch.neukom.bober.statlinesimulator.data.Army;
import ch.neukom.bober.statlinesimulator.data.Enhancement;
import ch.neukom.bober.statlinesimulator.data.Unit;
import ch.neukom.bober.statlinesimulator.display.Printable;

import java.util.Optional;
import java.util.Set;

public class StatisticsCalculator {
    private StatisticsCalculator() {
    }

    public static Statistics calculate(Army army, Unit unit) {
        Set<Enhancement> armyEnhancements = Optional.ofNullable(army)
            .map(Army::armyData)
            .map(Army.ArmyData::enhancements)
            .orElse(Set.of());
        return calculate(unit, armyEnhancements);
    }

    public static Statistics calculate(Unit unit, Set<Enhancement> armyEnhancements) {
        Integer skill = unit.getSkill(armyEnhancements);
        Integer accuracy = unit.getAccuracy(armyEnhancements);
        float hitChance = calculateHitChance(skill, accuracy);
        int attackCount = unit.count() * unit.getShots(armyEnhancements);
        return new Statistics(
            unit,
            hitChance,
            attackCount,
            hitChance * attackCount
        );
    }

    private static float calculateHitChance(Integer skill, Integer accuracy) {
        if (skill >= accuracy) {
            return 0;
        }

        float chanceToMiss = (float) skill / (float) accuracy;
        return 1 - chanceToMiss;
    }

    public record Statistics(
        Unit unit,
        float hitChancePerUnit,
        int attackCount,
        float averageHitsPerAttack
    ) implements Printable {
        @Override
        public String print() {
            return String.format(
                """
                    %s
                    - Shot Count: %s
                    - Hit Chance Per Shot: %.2f%%
                    - Average Hits Per Attack: %.0f""",
                unit().print(),
                attackCount(),
                hitChancePerUnit() * 100,
                Math.floor(averageHitsPerAttack())
            );
        }
    }
}
