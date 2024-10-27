package ch.neukom.bober.statlinesimulator.properties;

import org.testng.annotations.Test;

import java.io.InputStream;

import static com.google.common.truth.Truth.*;

public class PropertyLoaderTest {
    @Test
    public void testDefaultProperties() {
        assertThat(PropertyLoader.get()).isEqualTo(new BoberProperties(
            "boberData"
        ));
    }

    @Test
    public void getSpecificProperties() {
        InputStream propertiesStream = PropertyLoaderTest.class.getResourceAsStream("example.properties");
        assertThat(PropertyLoader.get("test", propertiesStream)).isEqualTo(new BoberProperties(
            "test-value"
        ));
    }
}
