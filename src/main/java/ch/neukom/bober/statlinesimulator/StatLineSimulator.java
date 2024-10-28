package ch.neukom.bober.statlinesimulator;

import ch.neukom.bober.statlinesimulator.data.Army;
import ch.neukom.bober.statlinesimulator.loader.DataLoader;
import ch.neukom.bober.statlinesimulator.statistics.StatisticsCalculator;

import java.util.Collection;
import java.util.Map;

public class StatLineSimulator {
    public static void main(String[] args) {
        DataLoader dataLoader = new DataLoader();
        Map<String, Army> armies = dataLoader.loadOnce();
        armies.values().stream().map(Army::print).forEach(System.out::println);

        System.out.println("---\nStatistics\n---\n");
        armies.values()
            .stream()
            .map(Army::units)
            .flatMap(Collection::stream)
            .map(StatisticsCalculator::calculate)
            .forEach(statistics -> System.out.printf(
                """
                    ---
                    %s
                    - Attack Count: %s
                    - Hit Chance Per Unit: %.2f%%
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
