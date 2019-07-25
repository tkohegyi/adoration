package org.rockhill.adorApp.exception.properties;


import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import org.rockhill.adorApp.configuration.PropertyHolder;
import org.rockhill.adorApp.configuration.PropertyReader;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Properties;

import static org.testng.Assert.assertEquals;

/**
 * Unit tests for the class {@link PropertyReader}.
 */
public class PropertyReaderTest {

    private Properties properties;
    private PropertyHolder propertyHolder;

    @InjectMocks
    private PropertyReader underTest;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        properties = new Properties();
        propertyHolder = new PropertyHolder();
        Whitebox.setInternalState(underTest, "propertyHolder", propertyHolder);
    }

    @Test
    public void testSetPropertiesShouldPutPropertiesToPropertyHolder() {
        //GIVEN in setUp
        properties.put("webapp.port", "1234");
        //WHEN
        underTest.setProperties(properties);
        //THEN
        PropertyHolder actual = (PropertyHolder) Whitebox.getInternalState(underTest, "propertyHolder");
        assertEquals(actual.getInt("webapp.port"), Integer.valueOf(1234));
    }
}
