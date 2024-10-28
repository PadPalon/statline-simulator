package ch.neukom.bober.statlinesimulator.data;

public enum WeaponIntegerCharacteristic implements TypedCharacteristic<Integer> {
    DAMAGE,
    AMMO,
    SHOTS,
    ACCURACY;

    @Override
    public Type<Integer> type() {
        return Type.INTEGER;
    }
}
