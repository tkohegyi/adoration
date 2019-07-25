package org.rockhill.adorApp.web.controller;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rockhill.adorApp.web.provider.LogFileProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeMethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

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

    @InjectMocks
    private AppLogController underTest;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    //@Test
    public void testGetLogFilesShouldRespondWithLogFilesWithJsonName() {
        //GIVEN
        Map<String, Collection<String>> expected = new HashMap<>();
        Collection<String> fileNames = new ArrayList<>();
        fileNames.add("a");
        expected.put(JSON_NAME, fileNames);
        given(logFileProvider.getLogFileNames()).willReturn(fileNames);
        //WHEN
        Map<String, Collection<String>> result = underTest.getLogFiles();
        //THEN
        assertEquals(expected, result);
    }

    //@Test
    public void testGetLogFileContentWhenSourceIsTrueShouldNotSetContentDisposition() {
        //GIVEN
        String expectedBody = "content";
        String fileName = "something";
        given(logFileProvider.getLogContent(fileName)).willReturn(expectedBody);
        //WHEN
        ResponseEntity<String> result = underTest.getLogFileContent(fileName, true, NOT_IMPORTANT);
        //THEN
        assertEquals(MediaType.TEXT_PLAIN, result.getHeaders().getContentType());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNull(result.getHeaders().get(CONTENT_DISPOSITION));
        assertEquals(expectedBody, result.getBody());
    }

    //    @Test
    public void testGetLogFileContentWhenSourceIsFalseShouldSetContentDispositionToAttachment() {
        //GIVEN
        String expectedBody = "content";
        String fileName = "something";
        given(logFileProvider.getLogContent(fileName)).willReturn(expectedBody);
        //WHEN
        ResponseEntity<String> result = underTest.getLogFileContent(fileName, false, NOT_IMPORTANT);
        //THEN
        assertEquals(MediaType.TEXT_PLAIN, result.getHeaders().getContentType());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(String.format(ATTACHMENT_TEMPLATE, fileName), result.getHeaders().getFirst(CONTENT_DISPOSITION));
        assertEquals(expectedBody, result.getBody());
    }

    //@Test
    public void testGetLogFileContentWhenUserIsOnWindowsShouldConvertLineBreaks() {
        //GIVEN
        String expectedBody = "content\r\n";
        String userAgentWindows = "SOMETHINGSOMETHING-WINDOWS";
        String body = "content\n";
        String fileName = "something";
        given(logFileProvider.getLogContent(fileName)).willReturn(body);
        //WHEN
        ResponseEntity<String> result = underTest.getLogFileContent(fileName, true, userAgentWindows);
        //THEN
        assertEquals(expectedBody, result.getBody());
    }
}
