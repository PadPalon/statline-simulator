package ch.neukom.bober.statlinesimulator.data;

public interface TypedCharacteristic<R extends Number> {
    String name();
    Type<R> type();

    interface Type<R> {
        Type<Integer> INTEGER = new IntegerType();
        class IntegerType implements Type<Integer> {
            private IntegerType() {}

            @Override
            public Integer cast(Number value) {
                return value.intValue();
            }
        }

        Type<Float> FLOAT = new FloatType();
        class FloatType implements Type<Float> {
            private FloatType() {}

            @Override
            public Float cast(Number value) {
                return value.floatValue();
            }
        }

        R cast(Number value);
    }
}
