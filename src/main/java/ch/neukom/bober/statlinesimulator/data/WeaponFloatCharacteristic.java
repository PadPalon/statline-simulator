package ch.neukom.bober.statlinesimulator.data;

public enum WeaponFloatCharacteristic implements TypedCharacteristic<Float> {
    RANGE;

    @Override
    public Type<Float> type() {
        return Type.FLOAT;
    }
}
