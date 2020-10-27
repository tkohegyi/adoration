package org.rockhill.adoration.initialize;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.rockhill.adoration.configuration.PropertyHolder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.doReturn;

/**
 * Unit tests for the class {@link EngineConfigurationAccess}.
 */
public class EngineConfigurationAccessTest {

    private PropertyDto properties;
    private Integer defaultPort = 8080;

    @Mock
    private PropertyHolder propertyHolder;

    @InjectMocks
    private EngineConfigurationAccess underTest;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetProperties() {
        //GIVEN
        properties = new PropertyDto(defaultPort);
        Whitebox.setInternalState(underTest, "properties", properties);
        //WHEN
        PropertyDto returnedProperty = underTest.getProperties();
        //THEN
        returnedProperty.getPort().equals(defaultPort);
    }

    @Test
    public void testGetPropertyHolder() {
        //GIVEN
        Whitebox.setInternalState(underTest, "propertyHolder", propertyHolder);
        doReturn(defaultPort).when(propertyHolder).getInt("webapp.port");
        //WHEN
        underTest.loadProperties();
        //THEN
        underTest.getProperties().getPort().equals(defaultPort);
    }

}
