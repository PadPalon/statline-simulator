package ch.neukom.bober.statlinesimulator.data;

public enum UnitIntegerCharacteristic implements TypedCharacteristic<Integer> {
    HEALTH,
    SKILL,
    DEFENSE,
    MORALE;

    @Override
    public Type<Integer> type() {
        return Type.INTEGER;
    }
}
