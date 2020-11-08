package org.rockhill.adoration.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.rockhill.adoration.web.json.CoverageInformationJson;
import org.rockhill.adoration.web.json.CurrentUserInformationJson;
import org.rockhill.adoration.web.provider.CoverageProvider;
import org.rockhill.adoration.web.provider.CurrentUserProvider;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

public class HomeControllerTest {

    @InjectMocks
    private HomeController underTest;
    @Mock
    private CoverageInformationJson coverageInformationJson;
    @Mock
    private CurrentUserProvider currentUserProvider;
    @Mock
    private CoverageProvider coverageProvider;
    @Mock
    private CurrentUserInformationJson currentUserInformationJson;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(underTest, "currentUserProvider", currentUserProvider);
        Whitebox.setInternalState(underTest, "coverageProvider", coverageProvider);
        doReturn(currentUserInformationJson).when(currentUserProvider).getUserInformation(null);
    }

    @Test
    public void pseudoHome() {
        //given - no precondition
        //when
        String result = underTest.pseudoHome();
        //then
        assertEquals("redirect:/adoration/", result); //redirect to real home
    }

    @Test
    public void realHome() {
        //given - no precondition
        //when
        String result = underTest.realHome();
        // then
        assertEquals("home", result);
    }

    @Test
    public void favicon() {
        //given - no precondition
        //when
        String result = underTest.favicon();
        // then
        assertEquals("redirect:/resources/img/favicon.ico", result);
    }

    @Test
    public void robots() {
        //given - no precondition
        //when
        String result = underTest.robots();
        // then
        assertEquals("redirect:/resources/robots.txt", result);
    }

    @Test
    public void securityText() {
        //given - no precondition
        //when
        String result = underTest.securityText();
        // then
        assertEquals("redirect:/resources/security.txt", result);
    }

    @Test
    public void e404() {
        //given - no precondition
        //when
        String result = underTest.e404(null);
        // then
        assertEquals("E404", result);
    }

    @Test
    public void e500() {
        //given - no precondition
        //when
        String result = underTest.e500(null);
        // then
        assertEquals("E404", result);
    }

    @Test
    public void getLoggedInUserInfo() {
        //given
        MockControllerBase mockControllerBase = new MockControllerBase();
        String expected = mockControllerBase.mockGetJsonString("loggedInUserInfo", currentUserInformationJson);
        //when
        ResponseEntity<String> result = underTest.getLoggedInUserInfo(null);
        //then
        String json = result.getBody();
        assertEquals(expected, json);
    }

    @Test
    public void getCoverageInformation() {
        //given
        doReturn(coverageInformationJson).when(coverageProvider).getCoverageInfo(currentUserInformationJson);
        MockControllerBase mockControllerBase = new MockControllerBase();
        String expected = mockControllerBase.mockGetJsonString("coverageInfo", coverageInformationJson);
        //when
        ResponseEntity<String> result = underTest.getCoverageInformation(null);
        //then
        String json = result.getBody();
        assertEquals(expected, json);
    }

}