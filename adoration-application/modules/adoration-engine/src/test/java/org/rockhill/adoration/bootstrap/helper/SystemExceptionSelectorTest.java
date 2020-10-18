package org.rockhill.adoration.bootstrap.helper;


import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rockhill.adoration.exception.SystemException;
import org.springframework.beans.factory.BeanCreationException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.BDDMockito.given;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Unit tests for the class {@link SystemExceptionSelector}.
 */
public class SystemExceptionSelectorTest {

    private SystemExceptionSelector underTest;

    @Mock
    private IOException applicationException;
    @Mock
    private SystemException invalidPropertyException;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BeanCreationException beanCreationException;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new SystemExceptionSelector();
    }

    @Test
    public void testGetSystemExceptionShouldReturnWithNullWhenExceptionCauseIsNull() {
        //GIVEN
        given(beanCreationException.getMostSpecificCause()).willReturn(invalidPropertyException);
        given(beanCreationException.getCause()).willReturn(null);
        //WHEN
        SystemException actual = underTest.getSystemException(beanCreationException);
        //THEN
        assertNull(actual);
    }

    @Test
    public void testGetSystemExceptionShouldReturnWithNullWhenExceptionCauseNotContainSystemException() {
        //GIVEN
        given(beanCreationException.getMostSpecificCause()).willReturn(applicationException);
        given(beanCreationException.getCause()).willReturn(applicationException, applicationException);
        //WHEN
        SystemException actual = underTest.getSystemException(beanCreationException);
        //THEN
        assertNull(actual);
    }

    @Test
    public void testGetSystemExceptionShouldReturnWithSystemExceptionWhenExceptionCauseContainsSystemException() {
        //GIVEN
        given(beanCreationException.getMostSpecificCause()).willReturn(invalidPropertyException);
        given(beanCreationException.getCause()).willReturn(invalidPropertyException, invalidPropertyException);
        //WHEN
        SystemException actual = underTest.getSystemException(beanCreationException);
        //THEN
        assertNotNull(actual);
    }
}
