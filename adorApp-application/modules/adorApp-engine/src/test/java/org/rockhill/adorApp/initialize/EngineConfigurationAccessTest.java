package org.rockhill.adorApp.initialize;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rockhill.adorApp.configuration.PropertyHolder;
import org.testng.annotations.BeforeMethod;

/**
 * Unit tests for the class {@link EngineConfigurationAccess}.
 */
public class EngineConfigurationAccessTest {

    @Mock
    private PropertyHolder propertyHolder;

    @InjectMocks
    private EngineConfigurationAccess underTest;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

}
