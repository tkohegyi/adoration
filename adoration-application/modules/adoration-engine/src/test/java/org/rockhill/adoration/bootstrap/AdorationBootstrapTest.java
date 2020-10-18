package org.rockhill.adoration.bootstrap;

import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.powermock.api.mockito.PowerMockito;
import org.rockhill.adoration.bootstrap.helper.SystemExceptionSelector;
import org.rockhill.adoration.database.SessionFactoryHelper;
import org.rockhill.adoration.exception.InvalidPropertyException;
import org.rockhill.adoration.exception.SystemException;
import org.rockhill.adoration.properties.PropertyLoader;
import org.rockhill.adoration.properties.helper.PropertiesNotAvailableException;
import org.rockhill.adoration.web.WebAppServer;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanCreationException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Properties;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;
import static org.powermock.reflect.Whitebox.setInternalState;

/**
 * Unit tests for the class {@link AdorationBootstrap}.
 */
//@PrepareForTest(SessionFactoryHelper.class)
public class AdorationBootstrapTest /* extends PowerMockTestCase */ {

    private static final String[] ARGS = {"conf.properties"};
    private static final String[] NO_ARGS = {};
    private static final String[] NOT_REAL_ARGS = {"try.this"};

    private Properties properties;
    @Mock
    private SystemExceptionSelector systemExceptionSelector;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BeanCreationException beanCreationException;
    @Mock
    private Logger logger;
    @Mock
    private WebAppServer webAppServer;
    @Mock
    private SystemException systemException;
    @Mock
    private PropertyLoader propertyLoader;
    @Mock
    private SessionFactoryHelper sessionFactoryHelper;

    @InjectMocks
    private AdorationBootstrap underTest;

    @BeforeMethod
    public void setUp() {
        underTest = Mockito.spy(new AdorationBootstrap());
        MockitoAnnotations.initMocks(this);
        setInternalState(underTest, "systemExceptionSelector", systemExceptionSelector);
        setInternalState(underTest, "propertyLoader", propertyLoader);
        setInternalState(underTest, "logger", logger);
        properties = new Properties();
        given(propertyLoader.loadProperties(ARGS[0])).willReturn(properties);
        doNothing().when(sessionFactoryHelper).initiateHibernateSessionFactory(null, null, null);
    }

    @Test
    public void testBootstrapShouldCallStart() {
        //GIVEN
        properties.setProperty("webapp.port", "8080");
        doReturn(webAppServer).when(underTest).createWebAppServer();
        doReturn(sessionFactoryHelper).when(underTest).createSessionFactoryHelper();
        //WHEN
        underTest.bootstrap(ARGS);
        //THEN
        verify(webAppServer).start();
    }

    @Test
    public void testBootstrapShouldLogError() {
        //GIVEN
        properties.setProperty("webapp.port", "8080");
        BeanCreationException exception = new BeanCreationException("exception");
        doReturn(webAppServer).when(underTest).createWebAppServer();
        doReturn(sessionFactoryHelper).when(underTest).createSessionFactoryHelper();
        willThrow(exception).given(webAppServer).createServer(8080, false, null, null);
        given(systemExceptionSelector.getSystemException(exception)).willReturn(systemException);
        //WHEN
        underTest.bootstrap(ARGS);
        //THEN
        verify(logger).error(Mockito.anyString());
    }

    @Test(expectedExceptions = BeanCreationException.class)
    public void testBootstrapShouldThrowException() {
        //GIVEN
        properties.setProperty("webapp.port", "8080");
        BeanCreationException exception = new BeanCreationException("exception");
        doReturn(webAppServer).when(underTest).createWebAppServer();
        doReturn(sessionFactoryHelper).when(underTest).createSessionFactoryHelper();
        willThrow(exception).given(webAppServer).createServer(8080, false, null, null);
        given(systemExceptionSelector.getSystemException(exception)).willReturn(null);
        //WHEN
        underTest.bootstrap(ARGS);
        //THEN it should throw exception
    }

    @Test
    public void testBootstrapShouldThrowExceptionWhenPropertyFileIsEmpty() {
        //GIVEN
        doReturn(webAppServer).when(underTest).createWebAppServer();
        doReturn(sessionFactoryHelper).when(underTest).createSessionFactoryHelper();
        //WHEN
        underTest.bootstrap(NO_ARGS);
        //THEN
        verify(logger).error(Mockito.contains("Configuration file was not specified as input argument!"));
    }

    @Test
    public void testBootstrapShouldThrowExceptionWhenPropertyFileNameIsIncorrect() {
        //GIVEN
        doReturn(webAppServer).when(underTest).createWebAppServer();
        doReturn(sessionFactoryHelper).when(underTest).createSessionFactoryHelper();
        //WHEN
        underTest.bootstrap(NOT_REAL_ARGS);
        //THEN it should throw exception
        verify(logger).error(Mockito.contains("Configuration file must be a properties file!"));
    }

    @Test
    public void testBootstrapWhenPortCannotBeParsedShouldThrowException() {
        //GIVEN
        properties.setProperty("webapp.port", "text");
        doReturn(webAppServer).when(underTest).createWebAppServer();
        doReturn(sessionFactoryHelper).when(underTest).createSessionFactoryHelper();
        //WHEN
        underTest.bootstrap(ARGS);
        //THEN
        verify(logger).error(Mockito.anyString());
    }
}
