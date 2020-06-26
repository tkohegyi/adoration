package org.rockhill.adorApp.bootstrap;

import org.mockito.*;
import org.mockito.internal.util.reflection.Whitebox;
import org.rockhill.adorApp.bootstrap.helper.SystemExceptionSelector;
import org.rockhill.adorApp.exception.SystemException;
import org.rockhill.adorApp.properties.PropertyLoader;
import org.rockhill.adorApp.web.WebAppServer;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanCreationException;
import org.testng.annotations.BeforeMethod;

import java.util.Properties;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the class {@link AdorAppBootstrap}.
 */
public class UooBootstrapTest {

    private static final String[] ARGS = {"uoo.conf.properties"};
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

    @InjectMocks
    private AdorAppBootstrap underTest;

    @BeforeMethod
    public void setUp() {
        underTest = Mockito.spy(new AdorAppBootstrap());
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(underTest, "systemExceptionSelector", systemExceptionSelector);
        Whitebox.setInternalState(underTest, "propertyLoader", propertyLoader);
        Whitebox.setInternalState(underTest, "logger", logger);
        properties = new Properties();
        given(propertyLoader.loadProperties(ARGS[0])).willReturn(properties);
    }

    //@Test
    public void testBootstrapShouldCallStart() {
        //GIVEN
        properties.setProperty("webapp.port", "8080");
        doReturn(webAppServer).when(underTest).createWebAppServer();
        //WHEN
        underTest.bootstrap(ARGS);
        //THEN
        verify(webAppServer).start();
    }

    //@Test
    public void testBootstrapShouldLogError() {
        //GIVEN
        properties.setProperty("webapp.port", "8080");
        BeanCreationException exception = new BeanCreationException("exception");
        doReturn(webAppServer).when(underTest).createWebAppServer();
        willThrow(exception).given(webAppServer).createServer(8080, false, "", "");
        given(systemExceptionSelector.getSystemException(exception)).willReturn(systemException);
        //WHEN
        underTest.bootstrap(ARGS);
        //THEN
        verify(logger).error(Mockito.anyString());
    }

    //@Test(expectedExceptions = BeanCreationException.class)
    public void testBootstrapShouldThrowException() {
        //GIVEN
        properties.setProperty("webapp.port", "8080");
        BeanCreationException exception = new BeanCreationException("exception");
        doReturn(webAppServer).when(underTest).createWebAppServer();
        willThrow(exception).given(webAppServer).createServer(8080, false, "", "");
        given(systemExceptionSelector.getSystemException(exception)).willReturn(null);
        //WHEN
        underTest.bootstrap(ARGS);
        //THEN it should throw exception
    }

    //@Test
    public void testBootstrapWhenPortCannotBeParsedShouldThrowException() {
        //GIVEN
        properties.setProperty("webapp.port", "text");
        doReturn(webAppServer).when(underTest).createWebAppServer();
        //WHEN
        underTest.bootstrap(ARGS);
        //THEN
        verify(logger).error(Mockito.anyString());
    }
}
