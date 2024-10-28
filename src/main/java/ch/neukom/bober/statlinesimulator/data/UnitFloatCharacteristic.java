package ch.neukom.bober.statlinesimulator.data;

public enum UnitFloatCharacteristic implements TypedCharacteristic<Float> {
    MOVEMENT,
    COMMAND;

    @Override
    public Type<Float> type() {
        return Type.FLOAT;
    }
}
