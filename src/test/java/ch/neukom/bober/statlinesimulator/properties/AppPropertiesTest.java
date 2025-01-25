package ch.neukom.bober.statlinesimulator.properties;

import org.testng.annotations.Test;

import java.io.InputStream;

import static com.google.common.truth.Truth.assertThat;

public class AppPropertiesTest {
    @Test
    public void testDefaultProperties() {
        assertThat(AppProperties.get().dataPath()).isEqualTo("boberData");
    }

    @Test
    public void getSpecificProperties() {
        InputStream propertiesStream = AppPropertiesTest.class.getResourceAsStream("example.properties");
        assertThat(propertiesStream).isNotNull();
        assertThat(AppProperties.get("test", propertiesStream).dataPath()).isEqualTo("test-value");
    }
}
