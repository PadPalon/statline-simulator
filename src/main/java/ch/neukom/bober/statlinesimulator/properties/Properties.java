package ch.neukom.bober.statlinesimulator.properties;

import ch.neukom.bober.statlinesimulator.data.*;
import ch.neukom.bober.statlinesimulator.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Properties {
    private static final Map<String, Properties> instances = new HashMap<>();

    private final BoberProperties boberProperties;

    public static BoberProperties get() {
        InputStream propertiesStream = Properties.class.getResourceAsStream("bober.properties");
        if (propertiesStream == null) {
            throw new IllegalStateException("bober.properties not found");
        } else {
            return get("default", propertiesStream);
        }
    }

    public static BoberProperties get(String id, InputStream propertiesStream) {
        return instances.computeIfAbsent(id, _id -> {
            try {
                java.util.Properties properties = new java.util.Properties();
                properties.load(propertiesStream);
                return new Properties(properties);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).getProperties();
    }

    public Properties(java.util.Properties properties) {
        this.boberProperties = new BoberProperties(
            properties.getProperty("dataPath", "boberData"),
            getBaseCharacteristic(properties, UnitIntegerCharacteristic.HEALTH),
            getBaseCharacteristic(properties, UnitIntegerCharacteristic.DEFENSE),
            getBaseCharacteristic(properties, UnitIntegerCharacteristic.SKILL),
            getBaseCharacteristic(properties, UnitIntegerCharacteristic.MORALE),
            getBaseCharacteristic(properties, UnitFloatCharacteristic.COMMAND),
            getBaseCharacteristic(properties, UnitFloatCharacteristic.MOVEMENT),
            getBaseCharacteristic(properties, WeaponIntegerCharacteristic.AMMO),
            getBaseCharacteristic(properties, WeaponIntegerCharacteristic.DAMAGE),
            getBaseCharacteristic(properties, WeaponIntegerCharacteristic.ACCURACY),
            getBaseCharacteristic(properties, WeaponIntegerCharacteristic.SHOTS),
            getBaseCharacteristic(properties, WeaponFloatCharacteristic.RANGE)
        );
    }

    private <R extends Number> R getBaseCharacteristic(java.util.Properties properties, TypedCharacteristic<R> characteristic) {
        String key = getBaseCharacteristicKey(characteristic);
        TypedCharacteristic.Type<R> type = characteristic.type();
        Optional<? extends Number> value = switch (type) {
            case TypedCharacteristic.Type.IntegerType i -> getCharacteristicAsInt(properties, key);
            case TypedCharacteristic.Type.FloatType f -> getCharacteristicAsFloat(properties, key);
            default -> Optional.empty();
        };
        return value.map(type::cast).orElseThrow();
    }

    private Optional<Float> getCharacteristicAsFloat(java.util.Properties properties, String key) {
        return Optional.ofNullable(properties.getProperty(key)).map(Float::valueOf);
    }

    private Optional<Integer> getCharacteristicAsInt(java.util.Properties properties, String key) {
        return Optional.ofNullable(properties.getProperty(key)).map(Integer::valueOf);
    }

    private String getBaseCharacteristicKey(TypedCharacteristic<?> characteristic) {
        return "base%s".formatted(StringUtil.withFirstLetterUpperCase(characteristic.name()));
    }

    private BoberProperties getProperties() {
        return boberProperties;
    }
}
