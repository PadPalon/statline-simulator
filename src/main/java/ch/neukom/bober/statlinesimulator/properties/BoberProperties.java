package ch.neukom.bober.statlinesimulator.properties;

public record BoberProperties(
    String dataPath,
    // unit base characteristics
    Integer baseHealth,
    Integer baseDefense,
    Integer baseSkill,
    Integer baseMorale,
    Float baseCommand,
    Float baseMovement,
    // weapon base characteristics
    Integer baseAmmo,
    Integer baseDamage,
    Integer baseAccuracy,
    Integer baseShots,
    Float baseRanger
) {
}
