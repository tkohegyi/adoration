package org.rockhill.adorApp.initialize;

import org.rockhill.adorApp.bootstrap.StartUpMessageGenerator;
import org.rockhill.adorApp.configuration.ConfigurationAccessBase;
import org.rockhill.adorApp.properties.PropertyLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 *
 */
@Component
public class ConfigurationInitializer {

    @Autowired
    private PropertyLoader propertyLoader;
    @Autowired
    private StartUpMessageGenerator startUpMessageGenerator;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private List<ConfigurationAccessBase> configurationAccesses;


    /**
     * This method reads in property file of adorApp.
     *
     * @throws Exception {@link PropertyLoader}, {@link ApplicationContext} can throw different exceptions.
     */
    @PostConstruct
    void afterPropertiesSet() throws Exception {
        propertyLoader.loadProperties();
        loadProperties();
        startUpMessageGenerator.logStartUpMessage();
    }

    private void loadProperties() {
        for (ConfigurationAccessBase configAccess : configurationAccesses) {
            configAccess.loadProperties();
        }
    }

}
