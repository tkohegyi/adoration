package org.rockhill.adoration.web.controller;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.rockhill.adoration.web.json.CurrentUserInformationJson;
import org.rockhill.adoration.web.provider.CurrentUserProvider;
import org.rockhill.adoration.web.provider.LogFileProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.junit.Test;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;

/**
 * Unit test for {@link AppLogController}.
 */
public class AppLogControllerTest {

    private static final String JSON_NAME = "files";
    private static final String NOT_IMPORTANT = "";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String ATTACHMENT_TEMPLATE = "attachment; filename=%s";
    @Mock
    private LogFileProvider logFileProvider;
    @Mock
    private CurrentUserProvider currentUserProvider;
    @Mock
    private CurrentUserInformationJson currentUserInformationJson;

    @InjectMocks
    private AppLogController underTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(underTest,"currentUserProvider", currentUserProvider);
        Whitebox.setInternalState(underTest,"logFileProvider", logFileProvider);
        doReturn(currentUserInformationJson).when(currentUserProvider).getUserInformation(null);
    }

    @Test
    public void testGetLogFilesShouldRespondWithLogFilesWithJsonName() {
        //GIVEN
        Map<String, Collection<String>> expected = new HashMap<>();
        Collection<String> fileNames = new ArrayList<>();
        fileNames.add("a");
        expected.put(JSON_NAME, fileNames);
        given(logFileProvider.getLogFileNames()).willReturn(fileNames);
        Whitebox.setInternalState(currentUserInformationJson,"isAdoratorAdmin", true);
        //WHEN
        Map<String, Collection<String>> result = underTest.getLogFiles(null);
        //THEN
        assertEquals(expected, result);
    }

    @Test
    public void testGetLogFileContentWhenSourceIsTrueShouldNotSetContentDisposition() {
        //GIVEN
        String expectedBody = "content";
        String fileName = "something";
        given(logFileProvider.getLogContent(fileName)).willReturn(expectedBody);
        Whitebox.setInternalState(currentUserInformationJson,"isAdoratorAdmin", true);
        //WHEN
        ResponseEntity<String> result = underTest.getLogFileContent(null, fileName, true, NOT_IMPORTANT);
        //THEN
        assertEquals(MediaType.TEXT_PLAIN, result.getHeaders().getContentType());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNull(result.getHeaders().get(CONTENT_DISPOSITION));
        assertEquals(expectedBody, result.getBody());
    }

    @Test
    public void testGetLogFileContentWhenSourceIsFalseShouldSetContentDispositionToAttachment() {
        //GIVEN
        String expectedBody = "content";
        String fileName = "something";
        given(logFileProvider.getLogContent(fileName)).willReturn(expectedBody);
        Whitebox.setInternalState(currentUserInformationJson,"isAdoratorAdmin", true);
        //WHEN
        ResponseEntity<String> result = underTest.getLogFileContent(null, fileName, false, NOT_IMPORTANT);
        //THEN
        assertEquals(MediaType.TEXT_PLAIN, result.getHeaders().getContentType());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(String.format(ATTACHMENT_TEMPLATE, fileName), result.getHeaders().getFirst(CONTENT_DISPOSITION));
        assertEquals(expectedBody, result.getBody());
    }

    @Test
    public void testGetLogFileContentWhenUserIsOnWindowsShouldConvertLineBreaks() {
        //GIVEN
        String expectedBody = "content\r\n";
        String userAgentWindows = "SOMETHINGSOMETHING-WINDOWS";
        String body = "content\n";
        String fileName = "something";
        given(logFileProvider.getLogContent(fileName)).willReturn(body);
        Whitebox.setInternalState(currentUserInformationJson,"isAdoratorAdmin", true);
        //WHEN
        ResponseEntity<String> result = underTest.getLogFileContent(null, fileName, true, userAgentWindows);
        //THEN
        assertEquals(expectedBody, result.getBody());
    }
}
