package ch.neukom.bober.statlinesimulator.properties;

import org.testng.annotations.Test;

import java.io.InputStream;

import static com.google.common.truth.Truth.*;

public class PropertiesTest {
    @Test
    public void testDefaultProperties() {
        assertThat(Properties.get().dataPath()).isEqualTo("boberData");
    }

    @Test
    public void getSpecificProperties() {
        InputStream propertiesStream = PropertiesTest.class.getResourceAsStream("example.properties");
        assertThat(propertiesStream).isNotNull();
        assertThat(Properties.get("test", propertiesStream).dataPath()).isEqualTo("test-value");
    }
}
