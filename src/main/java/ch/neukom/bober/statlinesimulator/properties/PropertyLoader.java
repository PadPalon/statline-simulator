package ch.neukom.bober.statlinesimulator.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyLoader {
    private static final Map<String, PropertyLoader> instances = new HashMap<>();

    private final BoberProperties boberProperties;

    public PropertyLoader(Properties properties) {
        this.boberProperties = new BoberProperties(
            properties.getProperty("dataPath", "boberData")
        );
    }

    public static BoberProperties get() {
        InputStream propertiesStream = PropertyLoader.class.getResourceAsStream("bober.properties");
        return get("default", propertiesStream);
    }

    public static BoberProperties get(String id, InputStream propertiesStream) {
        return instances.computeIfAbsent(id, _id -> {
            try {
                Properties properties = new Properties();
                properties.load(propertiesStream);
                return new PropertyLoader(properties);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).getProperties();
    }

    private BoberProperties getProperties() {
        return boberProperties;
    }

}
