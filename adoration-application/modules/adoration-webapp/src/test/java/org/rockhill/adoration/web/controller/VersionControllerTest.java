package org.rockhill.adoration.web.controller;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rockhill.adoration.configuration.VersionTitleProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.BDDMockito.given;
import static org.testng.AssertJUnit.assertEquals;

/**
 * Unit test for {@link VersionController}.
 */
public class VersionControllerTest {

    @Mock
    private VersionTitleProvider titleProvider;

    @InjectMocks
    private VersionController underTest;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetVersionShouldReturnVersionResponse() {
        //GIVEN
        given(titleProvider.getVersionTitle()).willReturn("version");
        //WHEN
        ResponseEntity<String> result = underTest.getVersion();
        //THEN
        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertEquals("{\"adorationApplicationVersion\":\"version\"}", result.getBody());
        assertEquals(MediaType.APPLICATION_JSON, result.getHeaders().getContentType());
    }

}
