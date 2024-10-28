package ch.neukom.bober.statlinesimulator;

import ch.neukom.bober.statlinesimulator.data.Army;
import ch.neukom.bober.statlinesimulator.loader.DataLoader;
import ch.neukom.bober.statlinesimulator.statistics.StatisticsCalculator;
import ch.neukom.bober.statlinesimulator.util.StreamUtil;

import java.util.Map;

public class StatLineSimulator {
    public static void main(String[] args) {
        DataLoader dataLoader = new DataLoader();
        Map<String, Army> armies = dataLoader.loadOnce();
        armies.values().stream().map(Army::print).forEach(System.out::println);

        System.out.println("---\nStatistics\n---\n");
        armies.values()
            .stream()
            .flatMap(StreamUtil.mapWithSource(Army::units))
            .map(armyUnit -> StatisticsCalculator.calculate(armyUnit.source(), armyUnit.mapping()))
            .forEach(statistics -> System.out.printf(
                """
                    ---
                    %s
                    - Shot Count: %s
                    - Hit Chance Per Shot: %.2f%%
                    - Average Hits Per Attack: %.0f
                    ---

                    """,
                statistics.unit().print(),
                statistics.attackCount(),
                statistics.hitChancePerUnit() * 100,
                Math.floor(statistics.averageHitsPerAttack())
            ));
    }
}
