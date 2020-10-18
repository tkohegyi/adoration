package org.rockhill.adoration.web;

import org.eclipse.jetty.server.Server;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.rockhill.adoration.web.service.ServerException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.powermock.reflect.Whitebox.setInternalState;

/**
 * Unit tests for the class {@link WebAppServer}.
 */
public class WebAppServerTest {

    private static final String EXCEPTION_MESSAGE = "exception message";

    @Mock
    private Server server;

    private WebAppServer underTest;

    @BeforeMethod
    public void setUp() {
        underTest = Mockito.spy(new WebAppServer());
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testStartShouldStartServer() throws Exception {
        //GIVEN
        doNothing().when(underTest).startJettyServer();
        //WHEN
        underTest.start();
        //THEN
        verify(underTest).startJettyServer();
    }

    @Test(expectedExceptions = ServerException.class)
    public void testStartShouldLogErrorWhenCannotStart() throws Exception {
        //GIVEN
        doThrow(new Exception()).when(underTest).startJettyServer();
        //WHEN
        underTest.start();
        //THEN it should throw exception
    }

    @Test(expectedExceptions = ServerException.class)
    public void testStopShouldThrowExceptionWhenWebAppCanNotBeStopped() throws Exception {
        //GIVEN
        setInternalState(underTest, "server", server);
        given(server.isStarted()).willReturn(true);
        Exception e = new Exception(EXCEPTION_MESSAGE);
        doThrow(e).when(underTest).stopJettyServer();
        //WHEN
        underTest.stop();
        //THEN it should throw exception
    }

    @Test
    public void testStopShouldCallStopJettyServer() throws Exception {
        //GIVEN
        setInternalState(underTest, "server", server);
        given(server.isStarted()).willReturn(true);
        doNothing().when(underTest).stopJettyServer();
        //WHEN
        underTest.stop();
        //THEN
        verify(underTest).stopJettyServer();
    }

    @Test
    public void testStopShouldDoNothingWhenServerIsNull() throws Exception {
        //GIVEN
        setInternalState(underTest, "server", (Object[])null);
        //WHEN
        underTest.stop();
        //THEN
        verify(underTest, never()).stopJettyServer();
    }

    @Test
    public void testStopShouldDoNothingWhenServerIsNotStarted() throws Exception {
        //GIVEN
        setInternalState(underTest, "server", server);
        given(server.isStarted()).willReturn(false);
        //WHEN
        underTest.stop();
        //THEN
        verify(underTest, never()).stopJettyServer();
    }
}
