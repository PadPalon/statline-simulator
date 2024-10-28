package ch.neukom.bober.statlinesimulator.statistics;

import ch.neukom.bober.statlinesimulator.data.Unit;

public class StatisticsCalculator {
    private StatisticsCalculator() {}

    public static Statistics calculate(Unit unit) {
        Integer skill = unit.getSkill();
        Integer accuracy = unit.getAccuracy();
        float hitChance = calculateHitChance(skill, accuracy);
        int attackCount = unit.count() * unit.getShots();
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
    ) {}
}
